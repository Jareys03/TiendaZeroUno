package com.example.mdai.services;

import com.example.mdai.model.Producto;
import com.example.mdai.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // =======================
    // CRUD básico
    // =======================

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

    // =======================
    // Casos de uso de catálogo
    // =======================

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return findAll();
        }
        return productoRepository.findByNombreContainingIgnoreCase(texto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> filtrarPorPrecio(Double min, Double max) {
        if (min == null && max == null) {
            return findAll();
        }
        if (min == null) min = 0.0;
        if (max == null) max = Double.MAX_VALUE;
        return productoRepository.findByPrecioBetween(min, max);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> ordenarPorPrecioAsc() {
        return productoRepository.findAllByOrderByPrecioAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> ordenarPorPrecioDesc() {
        return productoRepository.findAllByOrderByPrecioDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarAvanzado(String texto,
                                         Double minPrecio,
                                         Double maxPrecio) {

        // Para simplificar, partimos de todos y filtramos en memoria.
        List<Producto> productos = productoRepository.findAll();

        return productos.stream()
                // Filtro por texto (nombre)
                .filter(p -> {
                    if (texto == null || texto.isBlank()) return true;
                    String t = texto.toLowerCase();
                    return p.getNombre() != null &&
                            p.getNombre().toLowerCase().contains(t);
                })
                // Filtro por precio mínimo
                .filter(p -> minPrecio == null || p.getPrecio() >= minPrecio)
                // Filtro por precio máximo
                .filter(p -> maxPrecio == null || p.getPrecio() <= maxPrecio)
                .collect(Collectors.toList());
    }


}
