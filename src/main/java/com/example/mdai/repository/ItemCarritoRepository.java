package com.example.mdai.repository;

import com.example.mdai.model.ItemCarrito;
import com.example.mdai.model.Carrito;
import com.example.mdai.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    // lista con todos los items del carrito
    List<ItemCarrito> findByCarrito_Id(Long carritoId);

    // buscar si ya hay un item en el carrito para ese producto
    Optional<ItemCarrito> findByCarritoAndProducto(Carrito carrito, Producto producto);

    // Borrar todos los items del carrito
    long deleteByCarrito_Id(Long carritoId);
}
