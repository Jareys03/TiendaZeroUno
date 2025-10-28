package com.example.mdai.repository;

import com.example.mdai.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    // Buscar el carrito por el id del usuario asociado
    Optional<Carrito> findByUsuario_Id(Long usuarioId);

    // Comprobar si un usuario ya tiene carrito
    boolean existsByUsuario_Id(Long usuarioId);
}
