package com.example.mdai.controller;

import com.example.mdai.exception.ResourceNotFoundException;
import com.example.mdai.model.Producto;
import com.example.mdai.services.CategoriaService;
import com.example.mdai.services.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);
    private final CategoriaService categoriaService;

    public ProductoController(ProductoService productoService,
                              CategoriaService categoriaService){
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    /**
     * LISTA PARA USUARIO (o admin según sesión o parámetro URL)
     * URL: GET /productos
     */
    @GetMapping
    public String listarUsuario(Model model,
                                HttpSession session,
                                @RequestParam(value = "modoAdmin", required = false) Boolean modoParam,
                                @RequestParam(value = "texto", required = false) String texto,
                                @RequestParam(value = "categoriaId", required = false) Long categoriaId,
                                @RequestParam(value = "precioMin", required = false) Double precioMin,
                                @RequestParam(value = "precioMax", required = false) Double precioMax) {

        try {
            // Actualizamos modo admin en sesión si viene el parámetro
            if (modoParam != null) {
                if (Boolean.TRUE.equals(modoParam)) {
                    session.setAttribute("modoAdmin", true);
                } else {
                    session.removeAttribute("modoAdmin");
                }
            }

            Object modo = session.getAttribute("modoAdmin");
            boolean esAdmin = modo != null && Boolean.TRUE.equals(modo);

            // Búsqueda avanzada por texto + rango de precio
            List<Producto> productos = productoService.buscarAvanzado(texto, precioMin, precioMax);

            // Filtro adicional por categoría
            if (categoriaId != null) {
                productos = productos.stream()
                        .filter(p -> p.getCategoria() != null
                                && categoriaId.equals(p.getCategoria().getId()))
                        .collect(Collectors.toList());
            }

            model.addAttribute("productos", productos);
            model.addAttribute("categorias", categoriaService.findAll());

            model.addAttribute("modoAdmin", esAdmin);

            // Volcar de nuevo los filtros al modelo para mantenerlos en el formulario
            model.addAttribute("texto", texto);
            model.addAttribute("categoriaId", categoriaId);
            model.addAttribute("precioMin", precioMin);
            model.addAttribute("precioMax", precioMax);

            // Plantilla según modo
            if (esAdmin) {
                return "productos/lista_admin";
            }
            return "productos/lista";
        } catch (Exception e) {
            logger.error("Error al listar productos", e);
            model.addAttribute("error", "Ocurrió un error al cargar la lista de productos.");
            model.addAttribute("productos", List.of());
            model.addAttribute("categorias", categoriaService.findAll());
            return "productos/lista";
        }
    }


    /**
     * LISTA PARA ADMIN (ruta legacy)
     * URL: GET /productos/admin
     * -> mantiene compatibilidad: muestra la vista admin
     */
    @GetMapping("/admin")
    public String listarAdmin(Model model,
                              @RequestParam(value = "texto", required = false) String texto,
                              @RequestParam(value = "categoriaId", required = false) Long categoriaId,
                              @RequestParam(value = "precioMin", required = false) Double precioMin,
                              @RequestParam(value = "precioMax", required = false) Double precioMax) {

        try {
            List<Producto> productos = productoService.buscarAvanzado(texto, precioMin, precioMax);

            if (categoriaId != null) {
                productos = productos.stream()
                        .filter(p -> p.getCategoria() != null
                                && categoriaId.equals(p.getCategoria().getId()))
                        .collect(Collectors.toList());
            }

            model.addAttribute("productos", productos);
            model.addAttribute("categorias", categoriaService.findAll());
            model.addAttribute("modoAdmin", true);

            model.addAttribute("texto", texto);
            model.addAttribute("categoriaId", categoriaId);
            model.addAttribute("precioMin", precioMin);
            model.addAttribute("precioMax", precioMax);

            return "productos/lista_admin";
        } catch (Exception e) {
            logger.error("Error al listar productos en modo admin", e);
            model.addAttribute("error", "Ocurrió un error al cargar la lista de productos.");
            model.addAttribute("productos", List.of());
            model.addAttribute("categorias", categoriaService.findAll());
            model.addAttribute("modoAdmin", true);
            return "productos/lista_admin";
        }
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
