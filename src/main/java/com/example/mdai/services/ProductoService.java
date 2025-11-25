package com.example.mdai.services;

import com.example.mdai.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {

    // === CRUD básico ===
    List<Producto> findAll();
    Optional<Producto> findById(Long id);
    Producto save(Producto producto);
    Producto update(Long id, Producto producto);
    void deleteById(Long id);

    // === Casos de uso basados en tu entidad (nombre + precio) ===

    /**
     * Buscar productos por texto (en el nombre).
     */
    List<Producto> buscarPorTexto(String texto);

    /**
     * Filtrar productos por rango de precio.
     */
    List<Producto> filtrarPorPrecio(Double min, Double max);

    /**
     * Ordenar productos por precio ascendente.
     */
    List<Producto> ordenarPorPrecioAsc();

    /**
     * Ordenar productos por precio descendente.
     */
    List<Producto> ordenarPorPrecioDesc();

    /**
     * Búsqueda combinada: texto + rango de precio.
     */
    List<Producto> buscarAvanzado(String texto,
                                  Double minPrecio,
                                  Double maxPrecio);
}
