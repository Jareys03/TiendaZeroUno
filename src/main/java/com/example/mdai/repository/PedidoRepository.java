package com.example.mdai.repository;

import com.example.mdai.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Sustituye el método problemático por uno que sí existe en la entidad
    List<Pedido> findAllByOrderByIdDesc();

    Optional<Pedido> findByNumero(String numero);

    // Pedidos de un usuario concreto, ordenados del más nuevo al más viejo
    List<Pedido> findByUsuario_IdOrderByIdDesc(Long usuarioId);
}
