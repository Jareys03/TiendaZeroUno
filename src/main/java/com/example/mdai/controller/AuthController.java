package com.example.mdai.controller;

import com.example.mdai.model.Usuario;
import com.example.mdai.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        try {
            // Reutilizamos register.html que tiene login+registro
            return "register";
        } catch (Exception e) {
            logger.error("Error al mostrar login", e);
            model.addAttribute("error", "Ocurrió un error al cargar el formulario de login.");
            return "register";
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam String correo,
                        @RequestParam String password,
                        Model model,
                        HttpSession session,
                        org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        try {
            Optional<Usuario> opt = usuarioService.buscarPorCorreo(correo);
            if (opt.isEmpty()) {
                model.addAttribute("loginError", "Usuario no encontrado");
                model.addAttribute("loginCorreo", correo);
                return "register";
            }

            Usuario u = opt.get();
            // Comprobación simple de contraseña en texto plano
            if (u.getPassword() == null || !u.getPassword().equals(password)) {
                model.addAttribute("loginError", "Contraseña incorrecta");
                model.addAttribute("loginCorreo", correo);
                return "register";
            }

            // Login correcto: guardar en sesión
            session.setAttribute("usuarioLogeado", u);

            // Asegurar que la sesión contiene un carrito (mapa productoId -> cantidad)
            if (session.getAttribute("carrito") == null) {
                session.setAttribute("carrito", new java.util.HashMap<Long, Integer>());
            }

            // Mensaje flash de bienvenida
            redirectAttrs.addFlashAttribute("mensaje", "Bienvenido, " + u.getNombre() + "!");
            return "redirect:/productos";
        } catch (Exception e) {
            logger.error("Error durante el proceso de login para correo=" + correo, e);
            model.addAttribute("loginError", "Ocurrió un error al intentar iniciar sesión.");
            model.addAttribute("loginCorreo", correo);
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        try {
            session.invalidate();
            return "redirect:/";
        } catch (Exception e) {
            logger.error("Error al hacer logout", e);
            // Aunque falle, invalidamos lo posible y redirigimos
            try {
                session.removeAttribute("usuarioLogeado");
            } catch (Exception ignored) {}
            return "redirect:/";
        }
    }
}
