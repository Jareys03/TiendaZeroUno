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
    public String crear(@ModelAttribute("usuario") Usuario usuario, BindingResult br, Model model) {
        try {
            if (br.hasErrors()) return "usuarios/form";
            usuarioService.save(usuario);
            return "redirect:/usuarios";
        } catch (ResourceNotFoundException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al crear usuario", e);
            model.addAttribute("error", "Ocurrió un error al crear el usuario.");
            return "usuarios/form";
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
    public String actualizar(@PathVariable Long id, @ModelAttribute("usuario") Usuario usuario, BindingResult br, Model model) {
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
            // Si ocurre un error al eliminar, redirigimos igualmente a la lista y se puede mostrar un mensaje genérico
        }
        return "redirect:/usuarios";
    }
}