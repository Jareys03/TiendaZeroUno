package com.example.mdai.controller;

import com.example.mdai.exception.ResourceNotFoundException;
import com.example.mdai.exception.ServiceException;
import com.example.mdai.model.Direccion;
import com.example.mdai.model.Usuario;
import com.example.mdai.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // =========================
    // LISTAR
    // =========================
    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("usuarios", usuarioService.findAll());
            return "usuarios/lista"; // templates/usuarios/lista.html
        } catch (ResourceNotFoundException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al listar usuarios", e);
            model.addAttribute("usuarios", Collections.emptyList());
            model.addAttribute("error", "Ocurrió un error al listar los usuarios.");
            return "usuarios/lista";
        }
    }

    // =========================
    // NUEVO
    // =========================
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        try {
            model.addAttribute("usuario", new Usuario());
            return "usuarios/form"; // templates/usuarios/form.html
        } catch (ResourceNotFoundException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al preparar formulario nuevo usuario", e);
            model.addAttribute("error", "Ocurrió un error al preparar el formulario.");
            model.addAttribute("usuario", new Usuario());
            return "usuarios/form";
        }
    }

    // =========================
    // CREAR (registro público)
    // =========================
    @PostMapping
    public String crear(@ModelAttribute("usuario") Usuario usuario,
                        BindingResult br,
                        Model model,
                        HttpSession session,
                        RedirectAttributes redirectAttrs) {
        try {
            if (br.hasErrors()) {
                model.addAttribute("usuario", usuario);
                // para el registro público estamos usando la vista "register"
                return "register";
            }

            // Asegurarnos de que cada Dirección tenga referenciada la entidad Usuario
            if (usuario.getDirecciones() != null) {
                usuario.getDirecciones().removeIf(d ->
                        d == null ||
                                ((d.getCalle() == null || d.getCalle().isBlank()) &&
                                        (d.getCiudad() == null || d.getCiudad().isBlank()))
                );
                for (Direccion d : usuario.getDirecciones()) {
                    d.setUsuario(usuario);
                }
            }

            // Logging para depuración
            try {
                logger.info("Intentando registrar usuario: nombre='{}', correo='{}'",
                        usuario.getNombre(), usuario.getCorreo());
                if (usuario.getDirecciones() != null) {
                    logger.info("Usuario tiene {} direcciones", usuario.getDirecciones().size());
                    int idx = 0;
                    for (Direccion d : usuario.getDirecciones()) {
                        if (d == null) {
                            logger.info("Direccion[{}] = null", idx);
                        } else {
                            logger.info("Direccion[{}].calle='{}', ciudad='{}', usuario_set={}",
                                    idx, d.getCalle(), d.getCiudad(), d.getUsuario() != null);
                        }
                        idx++;
                    }
                } else {
                    logger.info("Usuario sin direcciones");
                }
            } catch (Exception logEx) {
                logger.warn("Error al loggear datos del usuario antes de registrar: {}", logEx.getMessage());
            }

            // Registrar usuario
            Usuario creado = usuarioService.registrarUsuario(usuario);

            // AUTLOGIN SIEMPRE TRAS CREAR
            session.setAttribute("usuarioLogeado", creado);

            // Asegurar carrito en sesión
            if (session.getAttribute("carrito") == null) {
                session.setAttribute("carrito", new HashMap<Long, Integer>());
            }

            // Asegurar que tras registrarse no quede activo el modo administrador
            try { session.removeAttribute("modoAdmin"); } catch (Exception ignored) {}

            // Mensaje de bienvenida
            redirectAttrs.addFlashAttribute(
                    "mensaje",
                    "Cuenta creada correctamente. ¡Bienvenido, " + creado.getNombre() + "!"
            );

            // Ir a lista de productos
            return "redirect:/productos";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuario);
            return "register";
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (ServiceException e) {
            logger.error("ServiceException al crear usuario", e);
            Throwable cause = e;
            StringBuilder sb = new StringBuilder();
            while (cause != null) {
                if (cause.getMessage() != null) {
                    if (sb.length() > 0) sb.append(" -> ");
                    sb.append(cause.getClass().getSimpleName()).append(": ").append(cause.getMessage());
                }
                cause = cause.getCause();
            }
            String detalle = sb.length() > 0 ? sb.toString() : "(sin detalle)";
            model.addAttribute("error", "Ocurrió un error interno al crear el usuario. Detalle: " + detalle);
            model.addAttribute("usuario", usuario);
            return "register";
        } catch (DataIntegrityViolationException e) {
            logger.error("Violación de integridad al crear usuario", e);
            model.addAttribute("error", "No se pudo crear el usuario: datos duplicados o inválidos.");
            model.addAttribute("usuario", usuario);
            return "register";
        } catch (Exception e) {
            logger.error("Error al crear usuario", e);
            Throwable cause = e;
            StringBuilder sb = new StringBuilder();
            while (cause != null) {
                if (cause.getMessage() != null) {
                    if (sb.length() > 0) sb.append(" -> ");
                    sb.append(cause.getClass().getSimpleName()).append(": ").append(cause.getMessage());
                }
                cause = cause.getCause();
            }
            String detalle = sb.length() > 0 ? sb.toString() : e.toString();
            model.addAttribute("error", "Ocurrió un error al crear el usuario. Detalle: " + detalle);
            model.addAttribute("usuario", usuario);
            return "register";
        }
    }

    // =========================
    // EDITAR (mostrar formulario)
    // =========================
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        try {
            Optional<Usuario> u = usuarioService.findById(id);
            if (u.isEmpty()) return "redirect:/usuarios";

            Usuario usuario = u.get();
            if (usuario.getDirecciones() == null) {
                usuario.setDirecciones(new ArrayList<>());
            }

            model.addAttribute("usuario", usuario);
            return "usuarios/form";
        } catch (ResourceNotFoundException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al editar usuario id=" + id, e);
            model.addAttribute("error", "Ocurrió un error al cargar el usuario para edición: " + e.getMessage());
            model.addAttribute("usuario", new Usuario());
            return "usuarios/form";
        }
    }

    // =========================
    // ACTUALIZAR
    // =========================
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute("usuario") Usuario usuario,
                             BindingResult br,
                             Model model,
                             RedirectAttributes redirectAttrs) {
        try {
            if (br.hasErrors()) return "usuarios/form";

            // Limpiar direcciones vacías y vincularlas al usuario
            if (usuario.getDirecciones() != null) {
                usuario.getDirecciones().removeIf(d ->
                        d == null ||
                                ((d.getCalle() == null || d.getCalle().isBlank()) &&
                                        (d.getCiudad() == null || d.getCiudad().isBlank()))
                );
                for (Direccion d : usuario.getDirecciones()) {
                    d.setUsuario(usuario);
                }
            }

            usuarioService.update(id, usuario);
            redirectAttrs.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
            return "redirect:/usuarios";

        } catch (DataIntegrityViolationException dive) {
            logger.error("Violación de integridad al actualizar usuario id=" + id, dive);
            redirectAttrs.addFlashAttribute(
                    "error",
                    "No se pudo actualizar el usuario: datos en conflicto (correo duplicado u otra restricción). Detalle: "
                            + dive.getMostSpecificCause().getMessage()
            );
            return "redirect:/usuarios";
        } catch (ResourceNotFoundException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al actualizar usuario id=" + id, e);
            redirectAttrs.addFlashAttribute("error", "Ocurrió un error al actualizar el usuario: " + e.getMessage());
            return "redirect:/usuarios";
        }
    }

    // =========================
    // ELIMINAR POR GET (enlace de la tabla)
    // =========================
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            usuarioService.deleteById(id);
            redirectAttrs.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
        } catch (DataIntegrityViolationException dive) {
            logger.error("Violación de integridad al eliminar usuario id=" + id, dive);
            redirectAttrs.addFlashAttribute(
                    "error",
                    "No se puede eliminar el usuario porque tiene datos relacionados (pedidos, referencias). Detalle: "
                            + dive.getMostSpecificCause().getMessage()
            );
        } catch (ResourceNotFoundException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al eliminar usuario id=" + id, e);
            redirectAttrs.addFlashAttribute("error", "Ocurrió un error al eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }

    // =========================
    // ELIMINAR POR POST (si usas form con CSRF)
    // =========================
    @PostMapping("/eliminar/{id}")
    public String eliminarPost(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            usuarioService.deleteById(id);
            redirectAttrs.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
        } catch (DataIntegrityViolationException dive) {
            logger.error("Violación de integridad al eliminar usuario id=" + id, dive);
            redirectAttrs.addFlashAttribute(
                    "error",
                    "No se puede eliminar el usuario porque tiene datos relacionados (pedidos, referencias). Detalle: "
                            + dive.getMostSpecificCause().getMessage()
            );
        } catch (ResourceNotFoundException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al eliminar usuario id=" + id, e);
            redirectAttrs.addFlashAttribute("error", "Ocurrió un error al eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }

    // =========================
    // DEBUG (opcional)
    // =========================
    @GetMapping("/debug/list")
    @ResponseBody
    public List<Usuario> debugList() {
        return usuarioService.findAll();
    }

}
