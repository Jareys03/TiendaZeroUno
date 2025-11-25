package com.example.mdai.services;

import com.example.mdai.model.Usuario;
import com.example.mdai.model.Direccion;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    // === CRUD básico ===
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    Usuario save(Usuario usuario);
    Usuario update(Long id, Usuario usuario);
    void deleteById(Long id);

    // === Casos de uso ===

    /**
     * Registrar un nuevo usuario.
     * Valida que el correo no esté ya en uso.
     */
    Usuario registrarUsuario(Usuario usuario);

    /**
     * Buscar usuario por correo.
     */
    Optional<Usuario> buscarPorCorreo(String correo);

    /**
     * Comprobar si ya existe un usuario con un correo dado.
     */
    boolean existeCorreo(String correo);

    /**
     * Añadir una dirección a un usuario (manteniendo la relación 1..N).
     */
    Usuario agregarDireccion(Long usuarioId, Direccion direccion);

    /**
     * Eliminar una dirección concreta de un usuario.
     */
    Usuario quitarDireccion(Long usuarioId, Long direccionId);
}
