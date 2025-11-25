package com.example.mdai.services;

import com.example.mdai.model.Carrito;

import java.util.List;
import java.util.Optional;

public interface CarritoService {

    // CRUD básico
    List<Carrito> findAll();
    Optional<Carrito> findById(Long id);
    Carrito save(Carrito carrito);
    Carrito update(Long id, Carrito carrito);
    void deleteById(Long id);

    // --- Lógica de carrito según casos de uso ---

    /**
     * Devuelve el carrito del usuario.
     * Si no existe, lo crea y lo guarda.
     */
    Carrito obtenerOCrearCarritoPorUsuario(Long usuarioId);

    /**
     * Añade un producto al carrito del usuario.
     * Si el producto ya está, suma cantidades (no crea línea duplicada).
     * Si el carrito no existe, lo crea.
     */
    Carrito agregarProducto(Long usuarioId, Long productoId, int cantidad);

    /**
     * Elimina un producto del carrito del usuario.
     */
    Carrito eliminarProducto(Long usuarioId, Long productoId);
}
