package com.example.mdai.controller;

import com.example.mdai.model.Pedido;
import com.example.mdai.model.Usuario;
import com.example.mdai.services.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class MisPedidosController {

    private static final Logger logger = LoggerFactory.getLogger(MisPedidosController.class);

    private final PedidoService pedidoService;

    public MisPedidosController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/mis-pedidos")
    public String verMisPedidos(HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttrs) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogeado");
            if (usuario == null) {
                redirectAttrs.addFlashAttribute("loginError",
                        "Debes iniciar sesión para ver tus pedidos");
                return "redirect:/login";
            }

            List<Pedido> pedidos = pedidoService.findByUsuario(usuario);
            model.addAttribute("pedidos", pedidos);
            model.addAttribute("usuario", usuario);

            return "pedidos/mis-pedidos"; // plantilla nueva
        } catch (Exception e) {
            logger.error("Error al cargar 'mis pedidos'", e);
            redirectAttrs.addFlashAttribute("error",
                    "No se pudieron cargar tus pedidos.");
            return "redirect:/productos";
        }
    }
}
