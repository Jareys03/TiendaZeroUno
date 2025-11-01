package com.example.mdai.repository;

import com.example.mdai.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Sustituye el método problemático por uno que sí existe en la entidad
    List<Pedido> findAllByOrderByIdDesc();

    Optional<Pedido> findByNumero(String numero);

    // Ejemplos útiles si quieres filtros por los campos que sí tienes
    // List<Pedido> findByTotalGreaterThanOrderByIdDesc(BigDecimal total);
    // List<Pedido> findByNumeroStartingWithOrderByIdDesc(String prefijo);
}
