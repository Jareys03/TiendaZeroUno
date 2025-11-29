package com.example.mdai.services;

import com.example.mdai.model.Pedido;
import com.example.mdai.model.Usuario;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PedidoService {

    // CRUD básico
    List<Pedido> findAll();
    Optional<Pedido> findById(Long id);
    Pedido save(Pedido pedido);
    Pedido update(Long id, Pedido pedido);
    void deleteById(Long id);

    // Extras útiles
    List<Pedido> findAllOrderByIdDesc();
    Optional<Pedido> findByNumero(String numero);
    List<Pedido> findByUsuario(Usuario usuario);

    /**
     * CU-06 — Procesar compra:
     * - Comprueba que el carrito tiene items.
     * - Calcula el total del carrito.
     * - Genera un número de pedido.
     * - Crea el Pedido y lo guarda.
     * - Vacía el carrito (deja de tener productos).
     */
    Pedido crearDesdeCarrito(Long carritoId);
    Pedido crearDesdeCarritoSesion(Map<Long, Integer> carrito, Usuario usuario);
    Pedido actualizarPedidoDesdeCarritoSesion(Long pedidoId, Map<Long, Integer> carrito);

}
