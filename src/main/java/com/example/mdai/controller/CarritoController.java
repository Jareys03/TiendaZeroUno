package com.example.mdai.controller;

import com.example.mdai.model.Producto;
import com.example.mdai.services.ProductoService;
import com.example.mdai.services.PedidoService;
import com.example.mdai.model.Usuario;
import com.example.mdai.model.Pedido;
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
    private final PedidoService pedidoService;

    private static final Logger logger = LoggerFactory.getLogger(CarritoController.class);

    public CarritoController(ProductoService productoService,
                             PedidoService pedidoService) {
        this.productoService = productoService;
        this.pedidoService = pedidoService;
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

    // Cargar el carrito a partir de un pedido existente (para que el admin pueda editarlo)
    @GetMapping("/desde-pedido/{id}")
    public String cargarDesdePedido(@PathVariable("id") Long pedidoId,
                                    HttpSession session,
                                    RedirectAttributes redirectAttrs) {
        try {
            Pedido pedido = pedidoService.findById(pedidoId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Pedido no encontrado con id " + pedidoId));

            // Vaciar carrito de sesión
            Map<Long, Integer> carrito = new HashMap<>();
            // Rellenar con los productos del pedido
            pedido.getDetalles().forEach(detalle ->
                    carrito.put(detalle.getProducto().getId(), detalle.getCantidad())
            );

            session.setAttribute("carrito", carrito);
            // Marcar que estamos editando este pedido
            session.setAttribute("pedidoEditandoId", pedidoId);

            redirectAttrs.addFlashAttribute("mensaje",
                    "Editando productos del pedido " + pedido.getNumero());
            return "redirect:/carrito";
        } catch (Exception e) {
            logger.error("Error al cargar pedido en carrito id=" + pedidoId, e);
            redirectAttrs.addFlashAttribute("error",
                    "No se pudo cargar el pedido en el carrito.");
            return "redirect:/pedidos";
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
    @PostMapping("/confirmar")
    public String confirmarCompra(HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttrs) {
        try {
            // 1) Comprobar que hay usuario logeado
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogeado");
            if (usuario == null) {
                redirectAttrs.addFlashAttribute("loginError",
                        "Debes iniciar sesión para confirmar la compra");
                return "redirect:/login";
            }

            // 2) Obtener carrito de la sesión
            Map<Long, Integer> carrito = obtenerCarrito(session);
            if (carrito.isEmpty()) {
                redirectAttrs.addFlashAttribute("error",
                        "No puedes confirmar un pedido con el carrito vacío");
                return "redirect:/carrito";
            }

            // 3) Ver si estamos editando un pedido existente
            Long pedidoEditandoId = (Long) session.getAttribute("pedidoEditandoId");
            Pedido pedido;

            if (pedidoEditandoId != null) {
                // 🔹 Actualizar pedido existente (caso admin)
                pedido = pedidoService.actualizarPedidoDesdeCarritoSesion(pedidoEditandoId, carrito);
                session.removeAttribute("pedidoEditandoId");

                // Vaciar carrito
                session.setAttribute("carrito", new HashMap<Long, Integer>());
                redirectAttrs.addFlashAttribute("mensaje", "Pedido actualizado correctamente");
                return "redirect:/pedidos/" + pedido.getId();
            } else {
                // 🔹 Crear pedido nuevo (flujo normal de usuario)
                pedido = pedidoService.crearDesdeCarritoSesion(carrito, usuario);

                // Vaciar carrito
                session.setAttribute("carrito", new HashMap<Long, Integer>());

                model.addAttribute("pedido", pedido);
                return "pedidos/confirmacion";
            }

        } catch (Exception e) {
            logger.error("Error al confirmar compra desde carrito", e);
            redirectAttrs.addFlashAttribute("error",
                    "Ocurrió un error al confirmar la compra.");
            return "redirect:/carrito";
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
