package com.example.mdai.services;

import com.example.mdai.model.Usuario;
import com.example.mdai.repository.UsuarioRepository;
import com.example.mdai.services.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {


    private final UsuarioRepository usuarioRepository;


    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return (List<Usuario>) usuarioRepository.findAll();
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }


    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }


    @Override
    public Usuario update(Long id, Usuario usuario) {
        return usuarioRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(usuario.getNombre());
                    existing.setCorreo(usuario.getCorreo());
                    existing.setDirecciones(usuario.getDirecciones());
                    return usuarioRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));
    }


    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
}
