package com.example.mdai.controller;

import com.example.mdai.exception.ResourceNotFoundException;
import com.example.mdai.exception.ServiceException;
import com.example.mdai.model.Direccion;
import com.example.mdai.model.Usuario;
import com.example.mdai.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Optional;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    // ---------- LOGIN ----------

    @GetMapping("/login")
    public String mostrarLogin(Model model, HttpSession session) {

        // Si ya está logueado, no tiene sentido mostrar el login
        if (session.getAttribute("usuarioLogeado") != null) {
            return "redirect:/productos";
        }

        // Para mostrar de nuevo el correo en caso de error
        if (!model.containsAttribute("loginCorreo")) {
            model.addAttribute("loginCorreo", "");
        }

        return "register";
    }

    @PostMapping("/login")
    public String login(@RequestParam String correo,
                        @RequestParam String password,
                        Model model,
                        HttpSession session,
                        RedirectAttributes redirectAttrs) {
        try {
            Optional<Usuario> opt = usuarioService.buscarPorCorreo(correo);
            if (opt.isEmpty()) {
                model.addAttribute("loginError", "Usuario no encontrado");
                model.addAttribute("loginCorreo", correo);
                return "register";
            }

            Usuario u = opt.get();

            // Contraseña incorrecta
            if (u.getPassword() == null || !u.getPassword().equals(password)) {
                model.addAttribute("loginError", "Contraseña incorrecta");
                model.addAttribute("loginCorreo", correo);
                return "register";
            }

            // Login correcto
            session.setAttribute("usuarioLogeado", u);

            // Asegurar carrito en sesión
            if (session.getAttribute("carrito") == null) {
                session.setAttribute("carrito", new HashMap<Long, Integer>());
            }

            redirectAttrs.addFlashAttribute("mensaje", "Bienvenid@, " + u.getNombre() + "!");

            // ---- LÓGICA ADMIN / NORMAL ----
            // ADMIN = correo "admin@zerouno.com" y password "admin"
            boolean esAdmin =
                    u.getCorreo() != null &&
                            u.getCorreo().equalsIgnoreCase("admin@zerouno.com") &&
                            "admin".equals(password);

            if (esAdmin) {
                session.setAttribute("modoAdmin", true);
                return "redirect:/admin";      // ADMIN → PANEL ADMIN
            } else {
                session.removeAttribute("modoAdmin");
                return "redirect:/productos";  // USUARIO NORMAL → LISTA PRODUCTOS
            }

        } catch (Exception e) {
            logger.error("Error durante el proceso de login para correo=" + correo, e);
            model.addAttribute("loginError", "Ocurrió un error al intentar iniciar sesión.");
            model.addAttribute("loginCorreo", correo);
            return "register";
        }
    }

    // ---------- REGISTRO ----------

    @GetMapping("/registro")
    public String mostrarRegistro(Model model, HttpSession session) {

        // Si ya está logueado, no tiene sentido registrarse otra vez
        if (session.getAttribute("usuarioLogeado") != null) {
            return "redirect:/productos";
        }

        if (!model.containsAttribute("usuario")) {
            Usuario u = new Usuario();
            // Preparamos una dirección vacía para el form
            u.getDirecciones().add(new Direccion());
            model.addAttribute("usuario", u);
        }
        return "registro"; // 👈 tu plantilla de "Crear cuenta · TiendaZeroUno"
    }

    @PostMapping("/registro")
    public String registrar(
            @ModelAttribute("usuario") Usuario usuario,
            HttpSession session,
            RedirectAttributes redirectAttrs,
            Model model) {

        try {
            // 1) Normalizar direcciones: quitar vacías y enlazar el usuario
            if (usuario.getDirecciones() != null) {

                // Eliminar direcciones completamente vacías
                usuario.getDirecciones().removeIf(d ->
                        d == null ||
                                (
                                        (d.getCalle() == null || d.getCalle().isBlank()) &&
                                                (d.getCiudad() == null || d.getCiudad().isBlank())
                                )
                );

                // Enlazar el usuario en cada dirección válida
                for (Direccion d : usuario.getDirecciones()) {
                    if (d != null) {
                        d.setUsuario(usuario);
                    }
                }
            }

            // 2) Usar la lógica de registro con validaciones
            Usuario creado = usuarioService.registrarUsuario(usuario);

            // 3) Autologin
            session.setAttribute("usuarioLogeado", creado);

            // 4) Asegurar carrito en sesión
            if (session.getAttribute("carrito") == null) {
                session.setAttribute("carrito", new HashMap<Long, Integer>());
            }

            // 5) Mensaje y redirección a productos
            redirectAttrs.addFlashAttribute(
                    "mensaje",
                    "Cuenta creada correctamente. ¡Bienvenido, " + creado.getNombre() + "!"
            );

            return "redirect:/productos";

        } catch (IllegalArgumentException e) {
            // Errores de validación (correo inválido, ya existe, etc.)
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuario);
            return "registro";
        } catch (ServiceException e) {
            // Errores de servicio más específicos
            logger.error("Error de servicio al registrar nuevo usuario correo=" + usuario.getCorreo(), e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuario);
            return "registro";
        } catch (Exception e) {
            // Cualquier otra cosa inesperada
            logger.error("Error inesperado al registrar nuevo usuario correo=" + usuario.getCorreo(), e);
            model.addAttribute("error", "Ocurrió un error al crear la cuenta.");
            model.addAttribute("usuario", usuario);
            return "registro";
        }
    }

    @GetMapping("/register")
    public String redirigirRegisterALogin() {
        return "redirect:/login";
    }


    // ---------- LOGOUT ----------

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        try {
            session.invalidate();
            return "redirect:/";
        } catch (Exception e) {
            logger.error("Error al hacer logout", e);
            try {
                session.removeAttribute("usuarioLogeado");
            } catch (Exception ignored) {}
            return "redirect:/";
        }
    }

    // ---------- PERFIL DE USUARIO (MI PERFIL) ----------

    @GetMapping("/mi-perfil")
    public String mostrarMiPerfil(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario logeado = (Usuario) session.getAttribute("usuarioLogeado");

        if (logeado == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para acceder a tu perfil.");
            return "redirect:/login";
        }

        try {
            Usuario usuarioBD = usuarioService.findById(logeado.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            model.addAttribute("usuario", usuarioBD);
            return "usuarios/mi-perfil"; // templates/usuarios/mi-perfil.html
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "No se ha podido cargar tu perfil.");
            return "redirect:/";
        } catch (Exception e) {
            logger.error("Error al mostrar el perfil de usuario", e);
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al cargar tu perfil.");
            return "redirect:/";
        }
    }

    @PostMapping("/mi-perfil")
    public String actualizarMiPerfil(@ModelAttribute("usuario") Usuario formUsuario,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {

        Usuario logeado = (Usuario) session.getAttribute("usuarioLogeado");

        if (logeado == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para actualizar tu perfil.");
            return "redirect:/login";
        }

        try {
            // Aseguramos que solo edita su propio usuario
            Long idUsuario = logeado.getId();
            formUsuario.setId(idUsuario);

            // No tocamos direcciones ni contraseña desde aquí
            formUsuario.setDirecciones(null);

            Usuario actualizado = usuarioService.update(idUsuario, formUsuario);

            // Actualizamos la sesión con los datos nuevos
            session.setAttribute("usuarioLogeado", actualizado);

            redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente.");
            return "redirect:/mi-perfil";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "No se ha encontrado tu usuario en el sistema.");
            return "redirect:/";
        } catch (ServiceException e) {
            logger.error("Error de servicio al actualizar perfil", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/mi-perfil";
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar el perfil", e);
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al actualizar tu perfil.");
            return "redirect:/mi-perfil";
        }
    }
}
