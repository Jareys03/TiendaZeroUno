package com.example.mdai.services;

import com.example.mdai.model.ItemCarrito;

import java.util.List;
import java.util.Optional;

public interface ItemCarritoService {

    // CRUD básico
    List<ItemCarrito> findAll();
    Optional<ItemCarrito> findById(Long id);
    ItemCarrito save(ItemCarrito itemCarrito);
    ItemCarrito update(Long id, ItemCarrito itemCarrito);
    void deleteById(Long id);

    // Extra: operaciones por carrito
    List<ItemCarrito> findByCarritoId(Long carritoId);

    /**
     * Elimina todos los items de un carrito concreto.
     * Devuelve cuántos ha borrado.
     */
    long deleteByCarritoId(Long carritoId);
}
