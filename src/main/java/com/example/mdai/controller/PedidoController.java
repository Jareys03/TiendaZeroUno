package com.example.mdai.controller;

import com.example.mdai.model.Pedido;
import com.example.mdai.services.PedidoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Collections;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // LISTAR
    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("pedidos", pedidoService.findAll());
            return "pedidos/lista"; // templates/pedidos/lista.html
        } catch (Exception e) {
            logger.error("Error al listar pedidos", e);
            model.addAttribute("pedidos", Collections.emptyList());
            model.addAttribute("error", "Ocurrió un error al listar los pedidos.");
            return "pedidos/lista";
        }
    }

    // NUEVO
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        try {
            model.addAttribute("pedido", new Pedido());
            return "pedidos/form"; // templates/pedidos/form.html
        } catch (Exception e) {
            logger.error("Error al preparar formulario nuevo pedido", e);
            model.addAttribute("error", "Ocurrió un error al preparar el formulario.");
            model.addAttribute("pedido", new Pedido());
            return "pedidos/form";
        }
    }

    // CREAR
    @PostMapping
    public String crear(@ModelAttribute("pedido") Pedido pedido, BindingResult br, Model model) {
        try {
            if (br.hasErrors()) return "pedidos/form";
            pedidoService.save(pedido);
            return "redirect:/pedidos";
        } catch (Exception e) {
            logger.error("Error al crear pedido", e);
            model.addAttribute("error", "Ocurrió un error al crear el pedido.");
            return "pedidos/form";
        }
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        try {
            Optional<Pedido> p = pedidoService.findById(id);
            if (p.isEmpty()) return "redirect:/pedidos";
            model.addAttribute("pedido", p.get());
            return "pedidos/form";
        } catch (Exception e) {
            logger.error("Error al editar pedido id=" + id, e);
            model.addAttribute("error", "Ocurrió un error al cargar el pedido para edición.");
            return "redirect:/pedidos";
        }
    }

    // ACTUALIZAR
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute("pedido") Pedido pedido,
                             BindingResult br, Model model) {
        try {
            if (br.hasErrors()) return "pedidos/form";
            pedidoService.update(id, pedido);
            return "redirect:/pedidos";
        } catch (Exception e) {
            logger.error("Error al actualizar pedido id=" + id, e);
            model.addAttribute("error", "Ocurrió un error al actualizar el pedido.");
            return "pedidos/form";
        }
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        try {
            pedidoService.deleteById(id);
        } catch (Exception e) {
            logger.error("Error al eliminar pedido id=" + id, e);
            // redirigir de todos modos
        }
        return "redirect:/pedidos";
    }
}
