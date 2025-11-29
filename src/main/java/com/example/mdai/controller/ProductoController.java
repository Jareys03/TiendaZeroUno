package com.example.mdai.controller;

import com.example.mdai.model.Producto;
import com.example.mdai.model.Categoria;
import com.example.mdai.services.ProductoService;
import com.example.mdai.services.CategoriaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;
import java.util.Collections;


@Controller
@RequestMapping("/productos")
public class ProductoController {


    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);


    public ProductoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }


    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("productos", productoService.findAll());
            return "productos/lista"; // templates/productos/lista.html
        } catch (Exception e) {
            logger.error("Error al listar productos", e);
            model.addAttribute("productos", Collections.emptyList());
            model.addAttribute("error", "Ocurrió un error al listar los productos.");
            return "productos/lista";
        }
    }


    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        try {
            model.addAttribute("producto", new Producto());
            model.addAttribute("categorias", categoriaService.findAll());
            return "productos/form"; // templates/productos/form.html
        } catch (Exception e) {
            logger.error("Error al preparar formulario nuevo producto", e);
            model.addAttribute("error", "Ocurrió un error al preparar el formulario.");
            model.addAttribute("producto", new Producto());
            return "productos/form";
        }
    }


    @PostMapping
    public String crear(@ModelAttribute("producto") Producto producto, BindingResult br, Model model,
                        @RequestParam(value = "categoriaId", required = false) Long categoriaId) {
        try {
            if (br.hasErrors()) return "productos/form";
            if (categoriaId != null) {
                Categoria c = categoriaService.findById(categoriaId).orElse(null);
                producto.setCategoria(c);
            }
            productoService.save(producto);
            model.addAttribute("mensaje", "Producto creado");
            return "redirect:/productos";
        } catch (Exception e) {
            logger.error("Error al crear producto", e);
            model.addAttribute("error", "Ocurrió un error al crear el producto.");
            return "productos/form";
        }
    }


    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        try {
            Optional<Producto> p = productoService.findById(id);
            if (p.isEmpty()) return "redirect:/productos";
            model.addAttribute("producto", p.get());
            model.addAttribute("categorias", categoriaService.findAll());
            return "productos/form";
        } catch (Exception e) {
            logger.error("Error al editar producto id=" + id, e);
            model.addAttribute("error", "Ocurrió un error al cargar el producto para edición.");
            return "redirect:/productos";
        }
    }


    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute("producto") Producto producto, BindingResult br, Model model,
                              @RequestParam(value = "categoriaId", required = false) Long categoriaId) {
        try {
            if (br.hasErrors()) return "productos/form";
            if (categoriaId != null) {
                Categoria c = categoriaService.findById(categoriaId).orElse(null);
                producto.setCategoria(c);
            }
            productoService.update(id, producto);
            model.addAttribute("mensaje", "Producto actualizado");
            return "redirect:/productos";
        } catch (Exception e) {
            logger.error("Error al actualizar producto id=" + id, e);
            model.addAttribute("error", "Ocurrió un error al actualizar el producto.");
            return "productos/form";
        }
    }


    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Model model) {
        try {
            productoService.deleteById(id);
            model.addAttribute("mensaje", "Producto eliminado");
        } catch (Exception e) {
            logger.error("Error al eliminar producto id=" + id, e);
            model.addAttribute("error", "Ocurrió un error al eliminar el producto.");
        }
        return "redirect:/productos";
    }
}
