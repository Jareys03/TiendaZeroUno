package com.example.mdai.controller;

import com.example.mdai.model.Producto;
import com.example.mdai.services.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final ProductoService productoService;
    private static final Logger logger = LoggerFactory.getLogger(CarritoController.class);

    public CarritoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> obtenerCarrito(HttpSession session) {
        Object atributo = session.getAttribute("carrito");
        if (atributo == null) {
            Map<Long, Integer> nuevo = new HashMap<>();
            session.setAttribute("carrito", nuevo);
            return nuevo;
        }
        try {
            return (Map<Long, Integer>) atributo;
        } catch (ClassCastException e) {
            logger.error("El atributo 'carrito' en sesión no tiene el formato esperado", e);
            Map<Long, Integer> nuevo = new HashMap<>();
            session.setAttribute("carrito", nuevo);
            return nuevo;
        }
    }

    // VER CARRITO
    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        try {
            Map<Long, Integer> carrito = obtenerCarrito(session);

            Map<Producto, Integer> lineas = new HashMap<>();
            double total = 0.0;

            for (Map.Entry<Long, Integer> entry : carrito.entrySet()) {
                Long productoId = entry.getKey();
                Integer cantidad = entry.getValue();

                Optional<Producto> prodOpt = productoService.findById(productoId);
                if (prodOpt.isPresent()) {
                    Producto p = prodOpt.get();
                    lineas.put(p, cantidad);
                    total += p.getPrecio() * cantidad;
                }
            }

            model.addAttribute("lineas", lineas);
            model.addAttribute("total", total);

            return "carrito/ver";  // templates/carrito/ver.html
        } catch (Exception e) {
            logger.error("Error al mostrar el carrito", e);
            model.addAttribute("lineas", new HashMap<>());
            model.addAttribute("total", 0.0);
            model.addAttribute("error", "Ocurrió un error al cargar el carrito.");
            return "carrito/ver";
        }
    }

    // AÑADIR AL CARRITO
    @PostMapping("/agregar")
    public String agregarAlCarrito(
            @RequestParam("productoId") Long productoId,
            @RequestParam(name = "cantidad", defaultValue = "1") int cantidad,
            HttpSession session,
            RedirectAttributes redirectAttrs) {
        try {
            // Requerir login: si no hay usuario en sesión, redirigir a /login con mensaje
            if (session.getAttribute("usuarioLogeado") == null) {
                redirectAttrs.addFlashAttribute("loginError", "Debes iniciar sesión para añadir productos al carrito");
                return "redirect:/login";
            }

            Map<Long, Integer> carrito = obtenerCarrito(session);

            int cantidadActual = carrito.getOrDefault(productoId, 0);
            carrito.put(productoId, cantidadActual + cantidad);

            redirectAttrs.addFlashAttribute("mensaje", "Producto añadido al carrito");
            return "redirect:/carrito";
        } catch (Exception e) {
            logger.error("Error al añadir producto al carrito id=" + productoId, e);
            redirectAttrs.addFlashAttribute("error", "No se pudo añadir el producto al carrito.");
            return "redirect:/productos";
        }
    }

    // ACTUALIZAR CANTIDAD
    @PostMapping("/actualizar")
    public String actualizarCantidad(
            @RequestParam("productoId") Long productoId,
            @RequestParam("cantidad") int cantidad,
            HttpSession session,
            RedirectAttributes redirectAttrs) {
        try {
            Map<Long, Integer> carrito = obtenerCarrito(session);

            if (cantidad <= 0) {
                carrito.remove(productoId);
            } else {
                carrito.put(productoId, cantidad);
            }

            redirectAttrs.addFlashAttribute("mensaje", "Carrito actualizado");
            return "redirect:/carrito";
        } catch (Exception e) {
            logger.error("Error al actualizar cantidad producto id=" + productoId, e);
            redirectAttrs.addFlashAttribute("error", "No se pudo actualizar el carrito.");
            return "redirect:/carrito";
        }
    }

    // ELIMINAR LÍNEA
    @GetMapping("/eliminar/{id}")
    public String eliminarLinea(
            @PathVariable("id") Long productoId,
            HttpSession session,
            RedirectAttributes redirectAttrs) {
        try {
            Map<Long, Integer> carrito = obtenerCarrito(session);
            carrito.remove(productoId);

            redirectAttrs.addFlashAttribute("mensaje", "Producto eliminado del carrito");
            return "redirect:/carrito";
        } catch (Exception e) {
            logger.error("Error al eliminar producto del carrito id=" + productoId, e);
            redirectAttrs.addFlashAttribute("error", "No se pudo eliminar el producto del carrito.");
            return "redirect:/carrito";
        }
    }

    // VACIAR CARRITO
    @GetMapping("/vaciar")
    public String vaciarCarrito(HttpSession session,
                                RedirectAttributes redirectAttrs) {
        try {
            session.setAttribute("carrito", new HashMap<Long, Integer>());
            redirectAttrs.addFlashAttribute("mensaje", "Carrito vaciado");
            return "redirect:/carrito";
        } catch (Exception e) {
            logger.error("Error al vaciar el carrito", e);
            redirectAttrs.addFlashAttribute("error", "No se pudo vaciar el carrito.");
            return "redirect:/carrito";
        }
    }
}
