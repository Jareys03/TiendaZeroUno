package com.example.mdai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {


    @GetMapping("/")
    public String inicio() {
        return "register"; // página pública de inicio con login/registro
    }

    @GetMapping("/admin")
    public String admin() {
        return "home"; // vista existente de administrador
    }
}
