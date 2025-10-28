package com.example.mdai.repository;

import com.example.mdai.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Búsqueda por nombre (contiene, case-insensitive)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Rango de precio
    List<Producto> findByPrecioBetween(double min, double max);
}
