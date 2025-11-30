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

import jakarta.servlet.http.HttpSession;
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
     * LISTA PARA USUARIO (o admin según sesión o parámetro URL)
     * URL: GET /productos
     * -> muestra lista de usuario o admin según el flag de sesión 'modoAdmin' o parámetro 'modoAdmin'
     */
    @GetMapping
    public String listarUsuario(Model model,
                                HttpSession session,
                                @RequestParam(value = "modoAdmin", required = false) Boolean modoParam) {
        model.addAttribute("productos", productoService.findAll());

        // Si viene el parámetro en la URL, actualizamos la sesión
        if (modoParam != null) {
            if (Boolean.TRUE.equals(modoParam)) {
                session.setAttribute("modoAdmin", true);
            } else {
                session.removeAttribute("modoAdmin");
            }
        }

        Object modo = session.getAttribute("modoAdmin");
        boolean esAdmin = modo != null && Boolean.TRUE.equals(modo);

        model.addAttribute("modoAdmin", esAdmin);

        if (esAdmin) {
            // si la sesión está en modo admin, mostrar la plantilla admin
            return "productos/lista_admin";
        }
        // vista normal de usuario
        return "productos/lista";
    }

    /**
     * LISTA PARA ADMIN (ruta legacy)
     * URL: GET /productos/admin
     * -> mantiene compatibilidad: muestra la vista admin
     */
    @GetMapping("/admin")
    public String listarAdmin(Model model) {
        model.addAttribute("productos", productoService.findAll());
        model.addAttribute("modoAdmin", true);
        return "productos/lista_admin";
    }

    /**
     * MOSTRAR FORMULARIO DE EDICIÓN (ADMIN)
     * URL: GET /productos/editar/{id}
     */
    @GetMapping("/editar/{id}")
    public String mostrarEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs,
                                @RequestParam(value = "soloPrecio", required = false) Boolean soloPrecio) {
        try {
            Optional<Producto> opt = productoService.findById(id);
            if (opt.isEmpty()) {
                redirectAttrs.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/productos/admin";
            }
            model.addAttribute("producto", opt.get());
            // pasar el flag al modelo (por defecto false)
            model.addAttribute("soloPrecio", soloPrecio != null && Boolean.TRUE.equals(soloPrecio));
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
     * ACTUALIZAR SOLO EL PRECIO (ADMIN) - permite actualizar precio desde la lista sin modificar otros campos
     * URL: POST /productos/editar-precio/{id}
     */
    @PostMapping("/editar-precio/{id}")
    public String actualizarPrecio(@PathVariable Long id,
                                   @RequestParam("precio") Double precio,
                                   RedirectAttributes redirectAttrs) {
        try {
            Optional<Producto> opt = productoService.findById(id);
            if (opt.isEmpty()) {
                redirectAttrs.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/productos/admin";
            }
            Producto existing = opt.get();
            existing.setPrecio(precio);
            productoService.save(existing);
            redirectAttrs.addFlashAttribute("mensaje", "Precio actualizado correctamente");
            return "redirect:/productos/admin";
        } catch (Exception e) {
            logger.error("Error al actualizar precio producto id=" + id, e);
            redirectAttrs.addFlashAttribute("error", "Ocurrió un error al actualizar el precio.");
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
