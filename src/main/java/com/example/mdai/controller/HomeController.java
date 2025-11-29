package com.example.mdai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;


@Controller
public class HomeController {


    @GetMapping("/")
    public String inicio(HttpSession session) {
        // Al volver al inicio quitamos modo admin (si existía)
        session.setAttribute("modoAdmin", Boolean.FALSE);
        return "register"; // página pública de inicio con login/registro
    }

    @GetMapping("/admin")
    public String admin(HttpSession session) {
        // Establecer modo administrador en la sesión para mostrar botones de admin en las vistas
        session.setAttribute("modoAdmin", Boolean.TRUE);
        return "home"; // vista existente de administrador
    }
}
