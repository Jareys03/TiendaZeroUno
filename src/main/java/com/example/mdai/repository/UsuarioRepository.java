package com.example.mdai.repository;

import com.example.mdai.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    // Buscar usuario por correo
    Optional<Usuario> findByCorreo(String correo);

    // Validar si existe un usuario con ese correo
    boolean existsByCorreo(String correo);
}
