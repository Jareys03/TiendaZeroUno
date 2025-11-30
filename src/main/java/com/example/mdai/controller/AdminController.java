package com.example.mdai.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    // Activar modo administrador en la sesión y mostrar la vista 'home'
    @GetMapping("/admin")
    public String adminEnable(HttpSession session) {
        session.setAttribute("modoAdmin", true);
        return "home"; // mostrar el panel de administración (home.html)
    }

    // Desactivar modo administrador y volver al listado público
    @GetMapping("/admin/salir")
    public String adminDisable(HttpSession session) {
        session.removeAttribute("modoAdmin");
        return "redirect:/productos";
    }
}
