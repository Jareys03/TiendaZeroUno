package com.example.mdai.controller;

import com.example.mdai.model.Pedido;
import com.example.mdai.services.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // LISTAR
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pedidos", pedidoService.findAll());
        return "pedidos/lista"; // templates/pedidos/lista.html
    }

    // NUEVO
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("pedido", new Pedido());
        return "pedidos/form"; // templates/pedidos/form.html
    }

    // CREAR
    @PostMapping
    public String crear(@ModelAttribute("pedido") Pedido pedido, BindingResult br) {
        if (br.hasErrors()) return "pedidos/form";
        pedidoService.save(pedido);
        return "redirect:/pedidos";
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Optional<Pedido> p = pedidoService.findById(id);
        if (p.isEmpty()) return "redirect:/pedidos";
        model.addAttribute("pedido", p.get());
        return "pedidos/form";
    }

    // ACTUALIZAR
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute("pedido") Pedido pedido,
                             BindingResult br) {
        if (br.hasErrors()) return "pedidos/form";
        pedidoService.update(id, pedido);
        return "redirect:/pedidos";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        pedidoService.deleteById(id);
        return "redirect:/pedidos";
    }
}
