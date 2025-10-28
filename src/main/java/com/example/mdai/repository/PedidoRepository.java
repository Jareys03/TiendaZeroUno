package com.example.mdai.repository;

import com.example.mdai.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Lista de pedidos de un usuario
    List<Pedido> findByUsuario_IdOrderByIdDesc(Long usuarioId);

    // Buscar por número de pedido
    Pedido findByNumero(String numero);
}
