package com.example.mdai.controller;

import com.example.mdai.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String panelAdmin(HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogeado");

        // Si no hay usuario logueado
        if (usuario == null) {
            return "redirect:/login?error=Debes+iniciar+sesion";
        }

        // ADMIN = correo admin@zerouno.com
        boolean esAdmin =
                usuario.getCorreo() != null &&
                        usuario.getCorreo().equalsIgnoreCase("admin@zerouno.com") &&
                        "admin".equals(usuario.getPassword());

        if (!esAdmin) {
            return "redirect:/productos";  // usuario normal intentando colarse
        }

        // Modo admin activo (por si lo usas en navbar)
        session.setAttribute("modoAdmin", true);
        model.addAttribute("usuarioLogeado", usuario);

        return "home";
    }

    @GetMapping("/admin/salir")
    public String adminDisable(HttpSession session) {
        session.removeAttribute("modoAdmin");
        return "redirect:/productos";
    }
}
