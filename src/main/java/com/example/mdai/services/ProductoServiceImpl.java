package com.example.mdai.services;

import com.example.mdai.model.Producto;
import com.example.mdai.repository.ProductoRepository;
import com.example.mdai.services.ProductoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {


    private final ProductoRepository productoRepository;


    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }


    @Override
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }


    @Override
    public Producto update(Long id, Producto producto) {
        return productoRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(producto.getNombre());
                    existing.setPrecio(producto.getPrecio());
                    return productoRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + id));
    }


    @Override
    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }
}
