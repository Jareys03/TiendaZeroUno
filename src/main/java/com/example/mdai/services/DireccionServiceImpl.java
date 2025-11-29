package com.example.mdai.services;

import com.example.mdai.exception.ResourceNotFoundException;
import com.example.mdai.exception.ServiceException;
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
        try {
            List<Direccion> result = new ArrayList<>();
            direccionRepository.findAll().forEach(result::add);
            return result;
        } catch (Exception e) {
            throw new ServiceException("Error al listar direcciones", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Direccion> findById(Long id) {
        try {
            return direccionRepository.findById(id);
        } catch (Exception e) {
            throw new ServiceException("Error al buscar dirección por id: " + id, e);
        }
    }

    @Override
    public Direccion save(Direccion direccion) {
        try {
            validarDireccion(direccion);
            return direccionRepository.save(direccion);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al guardar dirección", e);
        }
    }

    @Override
    public Direccion update(Long id, Direccion direccion) {
        try {
            validarDireccion(direccion);

            return direccionRepository.findById(id)
                    .map(existing -> {
                        existing.setCalle(direccion.getCalle().trim());
                        existing.setCiudad(direccion.getCiudad().trim());
                        existing.setUsuario(direccion.getUsuario());
                        return direccionRepository.save(existing);
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada con id: " + id));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al actualizar dirección id: " + id, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            direccionRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Error al eliminar dirección id: " + id, e);
        }
    }

    // ---------- MÉTODOS EXTRA ----------

    @Override
    @Transactional(readOnly = true)
    public List<Direccion> findByUsuario(Long usuarioId) {
        try {
            List<Direccion> result = new ArrayList<>();
            direccionRepository.findAll().forEach(dir -> {
                if (dir.getUsuario() != null &&
                        dir.getUsuario().getId().equals(usuarioId)) {
                    result.add(dir);
                }
            });
            return result;
        } catch (Exception e) {
            throw new ServiceException("Error al listar direcciones por usuario id: " + usuarioId, e);
        }
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
                .orElseThrow(() -> new IllegalArgumentException("Usuario no válido para esta dirección"));

        direccion.setCalle(direccion.getCalle().trim());
        direccion.setCiudad(direccion.getCiudad().trim());
        direccion.setUsuario(usuario);
    }
}
