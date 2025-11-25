package com.example.mdai.services;

import com.example.mdai.model.Direccion;

import java.util.List;
import java.util.Optional;

public interface DireccionService {

    List<Direccion> findAll();
    Optional<Direccion> findById(Long id);

    Direccion save(Direccion direccion);

    Direccion update(Long id, Direccion direccion);

    void deleteById(Long id);

    /**
     * Devuelve todas las direcciones asociadas a un usuario.
     */
    List<Direccion> findByUsuario(Long usuarioId);
}
