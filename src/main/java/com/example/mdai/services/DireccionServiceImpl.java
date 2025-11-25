package com.example.mdai.services;

import com.example.mdai.model.Direccion;
import com.example.mdai.model.Usuario;
import com.example.mdai.repository.DireccionRepository;
import com.example.mdai.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DireccionServiceImpl implements DireccionService {

    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;

    public DireccionServiceImpl(DireccionRepository direccionRepository,
                                UsuarioRepository usuarioRepository) {
        this.direccionRepository = direccionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ---------- CRUD ----------

    @Override
    @Transactional(readOnly = true)
    public List<Direccion> findAll() {
        List<Direccion> result = new ArrayList<>();
        direccionRepository.findAll().forEach(result::add);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Direccion> findById(Long id) {
        return direccionRepository.findById(id);
    }

    @Override
    public Direccion save(Direccion direccion) {

        validarDireccion(direccion);

        return direccionRepository.save(direccion);
    }

    @Override
    public Direccion update(Long id, Direccion direccion) {

        validarDireccion(direccion);

        return direccionRepository.findById(id)
                .map(existing -> {
                    existing.setCalle(direccion.getCalle().trim());
                    existing.setCiudad(direccion.getCiudad().trim());
                    existing.setUsuario(direccion.getUsuario());
                    return direccionRepository.save(existing);
                })
                .orElseThrow(() ->
                        new IllegalArgumentException("Dirección no encontrada con id: " + id));
    }

    @Override
    public void deleteById(Long id) {
        direccionRepository.deleteById(id);
    }

    // ---------- MÉTODOS EXTRA ----------

    @Override
    @Transactional(readOnly = true)
    public List<Direccion> findByUsuario(Long usuarioId) {

        List<Direccion> result = new ArrayList<>();
        direccionRepository.findAll().forEach(dir -> {
            if (dir.getUsuario() != null &&
                    dir.getUsuario().getId().equals(usuarioId)) {
                result.add(dir);
            }
        });

        return result;
    }

    // ---------- VALIDACIÓN ----------

    private void validarDireccion(Direccion direccion) {

        if (direccion.getCalle() == null || direccion.getCalle().trim().isEmpty()) {
            throw new IllegalArgumentException("La calle no puede estar vacía");
        }

        if (direccion.getCiudad() == null || direccion.getCiudad().trim().isEmpty()) {
            throw new IllegalArgumentException("La ciudad no puede estar vacía");
        }

        if (direccion.getUsuario() == null) {
            throw new IllegalArgumentException("La dirección debe estar asociada a un usuario");
        }

        Long userId = direccion.getUsuario().getId();

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuario no válido para esta dirección"));

        direccion.setCalle(direccion.getCalle().trim());
        direccion.setCiudad(direccion.getCiudad().trim());
        direccion.setUsuario(usuario);
    }
}
