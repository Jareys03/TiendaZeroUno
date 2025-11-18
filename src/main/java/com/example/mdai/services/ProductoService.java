package com.example.mdai.services;

import com.example.mdai.model.Producto;
import java.util.List;
import java.util.Optional;


public interface ProductoService {
    List<Producto> findAll();
    Optional<Producto> findById(Long id);
    Producto save(Producto producto);
    Producto update(Long id, Producto producto);
    void deleteById(Long id);
}
