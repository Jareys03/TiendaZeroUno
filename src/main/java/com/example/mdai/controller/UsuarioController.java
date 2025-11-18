package com.example.mdai.controller;

import com.example.mdai.model.Usuario;
import com.example.mdai.services.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;


@Controller
@RequestMapping("/usuarios")
public class UsuarioController {


    private final UsuarioService usuarioService;


    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuarios/lista"; // templates/usuarios/lista.html
    }


    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/form"; // templates/usuarios/form.html
    }


    @PostMapping
    public String crear(@ModelAttribute("usuario") Usuario usuario, BindingResult br) {
        if (br.hasErrors()) return "usuarios/form";
        usuarioService.save(usuario);
        return "redirect:/usuarios";
    }


    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Optional<Usuario> u = usuarioService.findById(id);
        if (u.isEmpty()) return "redirect:/usuarios";
        model.addAttribute("usuario", u.get());
        return "usuarios/form";
    }


    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute("usuario") Usuario usuario, BindingResult br) {
        if (br.hasErrors()) return "usuarios/form";
        usuarioService.update(id, usuario);
        return "redirect:/usuarios";
    }


    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return "redirect:/usuarios";
    }
}