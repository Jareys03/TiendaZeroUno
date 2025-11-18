package com.example.mdai.services;


import com.example.mdai.model.Usuario;
import java.util.List;
import java.util.Optional;


public interface UsuarioService {
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    Usuario save(Usuario usuario);
    Usuario update(Long id, Usuario usuario);
    void deleteById(Long id);
}