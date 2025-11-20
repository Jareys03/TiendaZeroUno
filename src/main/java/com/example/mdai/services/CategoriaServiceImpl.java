package com.example.mdai.services;

import com.example.mdai.model.Categoria;
import com.example.mdai.repository.CategoriaRepository;
import com.example.mdai.services.CategoriaService;
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
        return categoriaRepository.save(categoria);
    }

    @Override
    public Categoria update(Long id, Categoria categoria) {
        return categoriaRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(categoria.getNombre());
                    return categoriaRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con id: " + id));
    }

    @Override
    public void deleteById(Long id) {
        categoriaRepository.deleteById(id);
    }
}