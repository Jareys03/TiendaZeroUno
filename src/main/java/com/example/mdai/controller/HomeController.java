package com.example.mdai.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.mdai.model.Usuario;
import com.example.mdai.model.Direccion;


@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public String inicio(Model model) {
        try {
            // Crear un Usuario vacío con una Direccion vacía para que Thymeleaf pueda enlazar direcciones[0]
            Usuario u = new Usuario();
            u.getDirecciones().add(new Direccion());
            model.addAttribute("usuario", u);
            return "register"; // página pública de inicio con login/registro
        } catch (Exception e) {
            logger.error("Error al cargar la página de inicio", e);
            model.addAttribute("error", "Ocurrió un error al cargar la página de inicio.");
            return "register";
        }
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        try {
            return "home"; // vista existente de administrador
        } catch (Exception e) {
            logger.error("Error al cargar la página de administrador", e);
            model.addAttribute("error", "Ocurrió un error al cargar la página de administrador.");
            return "home";
        }
    }
}
