package com.example.mdai.services;

import com.example.mdai.exception.ResourceNotFoundException;
import com.example.mdai.exception.ServiceException;
import com.example.mdai.model.Categoria;
import com.example.mdai.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // ---------- CRUD BÁSICO ----------

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> findAll() {
        try {
            return categoriaRepository.findAll();
        } catch (Exception e) {
            throw new ServiceException("Error al listar categorías", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> findById(Long id) {
        try {
            return categoriaRepository.findById(id);
        } catch (Exception e) {
            throw new ServiceException("Error al buscar categoría por id: " + id, e);
        }
    }

    @Override
    public Categoria save(Categoria categoria) {
        try {
            String nombreNormalizado = normalizarNombre(categoria.getNombre());

            if (nombreNormalizado.isEmpty()) {
                throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
            }

            if (categoriaRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
                throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
            }

            categoria.setNombre(nombreNormalizado);
            return categoriaRepository.save(categoria);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al guardar categoría", e);
        }
    }

    @Override
    public Categoria update(Long id, Categoria categoria) {
        try {
            String nombreNormalizado = normalizarNombre(categoria.getNombre());

            if (nombreNormalizado.isEmpty()) {
                throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
            }

            return categoriaRepository.findById(id)
                    .map(existing -> {
                        // si cambia el nombre, comprobamos que no choque con otra categoría
                        if (!existing.getNombre().equalsIgnoreCase(nombreNormalizado) &&
                                categoriaRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
                            throw new IllegalArgumentException("Ya existe otra categoría con ese nombre");
                        }

                        existing.setNombre(nombreNormalizado);
                        return categoriaRepository.save(existing);
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al actualizar categoría id: " + id, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            categoriaRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Error al eliminar categoría id: " + id, e);
        }
    }

    // ---------- MÉTODOS EXTRA PARA CASOS DE USO ----------

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> findByNombre(String nombre) {
        try {
            String nombreNormalizado = normalizarNombre(nombre);
            if (nombreNormalizado.isEmpty()) {
                return Optional.empty();
            }
            return categoriaRepository.findByNombreIgnoreCase(nombreNormalizado);
        } catch (Exception e) {
            throw new ServiceException("Error al buscar categoría por nombre", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre) {
        try {
            String nombreNormalizado = normalizarNombre(nombre);
            if (nombreNormalizado.isEmpty()) {
                return false;
            }
            return categoriaRepository.existsByNombreIgnoreCase(nombreNormalizado);
        } catch (Exception e) {
            throw new ServiceException("Error al comprobar existencia de categoría", e);
        }
    }

    // ---------- HELPERS ----------

    private String normalizarNombre(String nombre) {
        return nombre == null ? "" : nombre.trim();
    }
}
