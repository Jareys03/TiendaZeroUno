package com.example.mdai.services;

import com.example.mdai.model.Categoria;
import com.example.mdai.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
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
        return categoriaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> findById(Long id) {
        return categoriaRepository.findById(id);
    }

    @Override
    public Categoria save(Categoria categoria) {
        String nombreNormalizado = normalizarNombre(categoria.getNombre());

        if (nombreNormalizado.isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }

        if (categoriaRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }

        categoria.setNombre(nombreNormalizado);
        return categoriaRepository.save(categoria);
    }

    @Override
    public Categoria update(Long id, Categoria categoria) {
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
                .orElseThrow(() ->
                        new IllegalArgumentException("Categoría no encontrada con id: " + id));
    }

    @Override
    public void deleteById(Long id) {
        categoriaRepository.deleteById(id);
    }

    // ---------- MÉTODOS EXTRA PARA CASOS DE USO ----------

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> findByNombre(String nombre) {
        String nombreNormalizado = normalizarNombre(nombre);
        if (nombreNormalizado.isEmpty()) {
            return Optional.empty();
        }
        return categoriaRepository.findByNombreIgnoreCase(nombreNormalizado);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre) {
        String nombreNormalizado = normalizarNombre(nombre);
        if (nombreNormalizado.isEmpty()) {
            return false;
        }
        return categoriaRepository.existsByNombreIgnoreCase(nombreNormalizado);
    }

    // ---------- HELPERS ----------

    private String normalizarNombre(String nombre) {
        return nombre == null ? "" : nombre.trim();
    }
}
