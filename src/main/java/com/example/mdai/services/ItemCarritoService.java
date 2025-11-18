package com.example.mdai.services;

import com.example.mdai.model.ItemCarrito;
import java.util.List;
import java.util.Optional;


public interface ItemCarritoService {
    List<ItemCarrito> findAll();
    Optional<ItemCarrito> findById(Long id);
    ItemCarrito save(ItemCarrito itemCarrito);
    ItemCarrito update(Long id, ItemCarrito itemCarrito);
    void deleteById(Long id);
}
