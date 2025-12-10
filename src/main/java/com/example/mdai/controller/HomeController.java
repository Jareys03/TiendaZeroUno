package com.example.mdai.controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public String inicio(HttpSession session) {
        try {
            // Si ya está logueado → directo a productos
            if (session.getAttribute("usuarioLogeado") != null) {
                return "redirect:/productos";
            }

            // Si no hay sesión → ir a login
            return "redirect:/register";

        } catch (Exception e) {
            logger.error("Error al cargar la página de inicio", e);
            return "redirect:/register";
        }
    }
}
