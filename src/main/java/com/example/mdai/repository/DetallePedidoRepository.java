package com.example.mdai.repository;

import com.example.mdai.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    // Por si quieres pedir los detalles de un pedido concreto
    List<DetallePedido> findByPedido_Id(Long pedidoId);

    // borrar detalles por producto id (para limpiar antes de eliminar un producto)
    long deleteByProducto_Id(Long productoId);
}