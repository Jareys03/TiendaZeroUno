package com.example.mdai.services;

import com.example.mdai.exception.ResourceNotFoundException;
import com.example.mdai.exception.ServiceException;
import com.example.mdai.model.Direccion;
import com.example.mdai.model.Usuario;
import com.example.mdai.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // =======================
    // CRUD básico
    // =======================

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        try {
            return (List<Usuario>) usuarioRepository.findAll();
        } catch (Exception e) {
            throw new ServiceException("Error al listar usuarios", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) {
        try {
            return usuarioRepository.findById(id);
        } catch (Exception e) {
            throw new ServiceException("Error al buscar usuario por id: " + id, e);
        }
    }

    @Override
    public Usuario save(Usuario usuario) {
        try {
            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            throw new ServiceException("Error al guardar usuario", e);
        }
    }

    @Override
    public Usuario update(Long id, Usuario usuario) {
        try {
            return usuarioRepository.findById(id)
                    .map(existing -> {
                        existing.setNombre(usuario.getNombre());
                        existing.setCorreo(usuario.getCorreo());
                        // las direcciones se gestionan con agregarDireccion/quitarDireccion
                        return usuarioRepository.save(existing);
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al actualizar usuario id: " + id, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            usuarioRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Error al eliminar usuario id: " + id, e);
        }
    }

    // =======================
    // Casos de uso
    // =======================

    @Override
    public Usuario registrarUsuario(Usuario usuario) {
        try {
            if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
                throw new IllegalArgumentException("El correo es obligatorio para registrar un usuario");
            }
            if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
                throw new IllegalArgumentException("Ya existe un usuario con el correo: " + usuario.getCorreo());
            }
            return usuarioRepository.save(usuario);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al registrar usuario", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCorreo(String correo) {
        try {
            return usuarioRepository.findByCorreo(correo);
        } catch (Exception e) {
            throw new ServiceException("Error al buscar usuario por correo: " + correo, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeCorreo(String correo) {
        try {
            return usuarioRepository.existsByCorreo(correo);
        } catch (Exception e) {
            throw new ServiceException("Error al comprobar existencia de correo: " + correo, e);
        }
    }

    @Override
    public Usuario agregarDireccion(Long usuarioId, Direccion direccion) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));

            usuario.agregarDireccion(direccion); // helper de la entidad Usuario
            return usuarioRepository.save(usuario);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al agregar dirección al usuario id: " + usuarioId, e);
        }
    }

    @Override
    public Usuario quitarDireccion(Long usuarioId, Long direccionId) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));

            Iterator<Direccion> it = usuario.getDirecciones().iterator();
            boolean removed = false;
            while (it.hasNext()) {
                Direccion d = it.next();
                if (d.getId() != null && d.getId().equals(direccionId)) {
                    it.remove();
                    d.setUsuario(null);
                    removed = true;
                    break;
                }
            }

            if (!removed) {
                throw new ResourceNotFoundException("Dirección no encontrada con id: " + direccionId);
            }

            return usuarioRepository.save(usuario);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al quitar dirección id: " + direccionId + " del usuario id: " + usuarioId, e);
        }
    }
}
