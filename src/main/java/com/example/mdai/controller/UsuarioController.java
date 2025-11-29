package com.example.mdai.controller;

import com.example.mdai.exception.ResourceNotFoundException;
import com.example.mdai.exception.ServiceException;
import com.example.mdai.model.Usuario;
import com.example.mdai.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Collections;
import com.example.mdai.model.Direccion;
import org.springframework.dao.DataIntegrityViolationException;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

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

            // 🔹 AUTLOGIN SIEMPRE TRAS CREAR
            session.setAttribute("usuarioLogeado", creado);

            // 🔹 Asegurar carrito en sesión
            if (session.getAttribute("carrito") == null) {
                session.setAttribute("carrito", new HashMap<Long, Integer>());
            }

            // Mensaje de bienvenida
            redirectAttrs.addFlashAttribute(
                    "mensaje",
                    "Cuenta creada correctamente. ¡Bienvenido, " + creado.getNombre() + "!"
            );

            // 🔹 SIEMPRE → lista de productos
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

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        try {
            Optional<Usuario> u = usuarioService.findById(id);
            if (u.isEmpty()) return "redirect:/usuarios";
            model.addAttribute("usuario", u.get());
            return "usuarios/form";
        } catch (ResourceNotFoundException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al editar usuario id=" + id, e);
            model.addAttribute("error", "Ocurrió un error al cargar el usuario para edición.");
            return "redirect:/usuarios";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute("usuario") Usuario usuario,
                             BindingResult br,
                             Model model) {
        try {
            if (br.hasErrors()) return "usuarios/form";
            usuarioService.update(id, usuario);
            return "redirect:/usuarios";
        } catch (ResourceNotFoundException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al actualizar usuario id=" + id, e);
            model.addAttribute("error", "Ocurrió un error al actualizar el usuario.");
            return "usuarios/form";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        try {
            usuarioService.deleteById(id);
        } catch (ResourceNotFoundException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al eliminar usuario id=" + id, e);
        }
        return "redirect:/usuarios";
    }
}
