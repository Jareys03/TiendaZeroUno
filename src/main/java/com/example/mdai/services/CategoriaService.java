package com.example.mdai.services;

import com.example.mdai.model.Categoria;
import java.util.List;
import java.util.Optional;


public interface CategoriaService {
    List<Categoria> findAll();
    Optional<Categoria> findById(Long id);
    Categoria save(Categoria categoria);
    Categoria update(Long id, Categoria categoria);
    void deleteById(Long id);
}