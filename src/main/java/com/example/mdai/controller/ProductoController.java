package com.example.mdai.controller;

import com.example.mdai.model.Producto;
import com.example.mdai.services.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;


@Controller
@RequestMapping("/productos")
public class ProductoController {


    private final ProductoService productoService;


    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }


    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.findAll());
        return "productos/lista"; // templates/productos/lista.html
    }


    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        return "productos/form"; // templates/productos/form.html
    }


    @PostMapping
    public String crear(@ModelAttribute("producto") Producto producto, BindingResult br) {
        if (br.hasErrors()) return "productos/form";
        productoService.save(producto);
        return "redirect:/productos";
    }


    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Optional<Producto> p = productoService.findById(id);
        if (p.isEmpty()) return "redirect:/productos";
        model.addAttribute("producto", p.get());
        return "productos/form";
    }


    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute("producto") Producto producto, BindingResult br) {
        if (br.hasErrors()) return "productos/form";
        productoService.update(id, producto);
        return "redirect:/productos";
    }


    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        productoService.deleteById(id);
        return "redirect:/productos";
    }
}
