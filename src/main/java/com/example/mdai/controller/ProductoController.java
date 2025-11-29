package com.example.mdai.controller;

import com.example.mdai.exception.ResourceNotFoundException;
import com.example.mdai.model.Producto;
import com.example.mdai.services.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * LISTA PARA USUARIO NORMAL
     * URL: GET /productos
     * -> muestra lista con botón "Añadir" (modoAdmin = false)
     */
    @GetMapping
    public String listarUsuario(Model model) {
        model.addAttribute("productos", productoService.findAll());
        // vista de usuario
        model.addAttribute("modoAdmin", false);
        return "productos/lista";
    }

    /**
     * LISTA PARA ADMIN
     * URL: GET /productos/admin
     * -> muestra lista con botones Editar / Eliminar (modoAdmin = true)
     */
    @GetMapping("/admin")
    public String listarAdmin(Model model) {
        model.addAttribute("productos", productoService.findAll());
        // vista de administración
        model.addAttribute("modoAdmin", true);
        return "productos/lista";
    }

    /**
     * MOSTRAR FORMULARIO DE EDICIÓN (ADMIN)
     * URL: GET /productos/editar/{id}
     */
    @GetMapping("/editar/{id}")
    public String mostrarEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        try {
            Optional<Producto> opt = productoService.findById(id);
            if (opt.isEmpty()) {
                redirectAttrs.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/productos/admin";
            }
            model.addAttribute("producto", opt.get());
            return "productos/form";  // formulario donde podrás editar el precio (y lo que quieras)
        } catch (Exception e) {
            logger.error("Error al cargar producto para edición id=" + id, e);
            redirectAttrs.addFlashAttribute("error", "Ocurrió un error al cargar el producto.");
            return "redirect:/productos/admin";
        }
    }

    /**
     * GUARDAR CAMBIOS (ADMIN)
     * URL: POST /productos/editar/{id}
     */
    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute("producto") Producto productoForm,
                             RedirectAttributes redirectAttrs) {
        try {
            productoService.update(id, productoForm);  // aquí se actualizará el precio también
            redirectAttrs.addFlashAttribute("mensaje", "Producto actualizado correctamente");
            return "redirect:/productos/admin";
        } catch (ResourceNotFoundException e) {
            redirectAttrs.addFlashAttribute("error", "Producto no encontrado");
            return "redirect:/productos/admin";
        } catch (Exception e) {
            logger.error("Error al actualizar producto id=" + id, e);
            redirectAttrs.addFlashAttribute("error", "Ocurrió un error al actualizar el producto.");
            return "redirect:/productos/admin";
        }
    }

    /**
     * ELIMINAR (ADMIN)
     * URL: GET /productos/eliminar/{id}
     */
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            productoService.deleteById(id);
            redirectAttrs.addFlashAttribute("mensaje", "Producto eliminado correctamente");
        } catch (ResourceNotFoundException e) {
            redirectAttrs.addFlashAttribute("error", "Producto no encontrado");
        } catch (Exception e) {
            logger.error("Error al eliminar producto id=" + id, e);
            redirectAttrs.addFlashAttribute("error", "Ocurrió un error al eliminar el producto.");
        }
        return "redirect:/productos/admin";
    }
}
