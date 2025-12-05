package com.example.mdai.services;

import com.example.mdai.exception.ResourceNotFoundException;
import com.example.mdai.exception.ServiceException;
import com.example.mdai.model.Producto;
import com.example.mdai.repository.ProductoRepository;
import com.example.mdai.repository.ItemCarritoRepository;
import com.example.mdai.repository.DetallePedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProductoServiceImpl.class);

    public ProductoServiceImpl(ProductoRepository productoRepository,
                               ItemCarritoRepository itemCarritoRepository,
                               DetallePedidoRepository detallePedidoRepository) {
        this.productoRepository = productoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    // =======================
    // CRUD básico
    // =======================

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        try {
            return productoRepository.findAll();
        } catch (Exception e) {
            throw new ServiceException("Error al listar productos", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> findById(Long id) {
        try {
            return productoRepository.findById(id);
        } catch (Exception e) {
            throw new ServiceException("Error al buscar producto por id: " + id, e);
        }
    }

    @Override
    public Producto save(Producto producto) {
        try {
            return productoRepository.save(producto);
        } catch (Exception e) {
            throw new ServiceException("Error al guardar producto", e);
        }
    }

    @Override
    public Producto update(Long id, Producto producto) {
        try {
            return productoRepository.findById(id)
                    .map(existing -> {
                        existing.setNombre(producto.getNombre());
                        existing.setPrecio(producto.getPrecio());
                        existing.setCategoria(producto.getCategoria());
                        return productoRepository.save(existing);
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al actualizar producto id: " + id, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            // Limpiar items de carritos que referencian este producto (FK item_carrito.producto_id)
            try {
                long removedItems = itemCarritoRepository.deleteByProducto_Id(id);
                logger.info("Eliminados {} items de carrito que referenciaban producto id={}", removedItems, id);
            } catch (Exception e) {
                logger.warn("No se pudieron eliminar items de carrito para producto id={}: {}", id, e.getMessage());
            }

            // Limpiar detalles de pedido que referencian este producto (FK detalle_pedido.producto_id)
            try {
                long removedDetalles = detallePedidoRepository.deleteByProducto_Id(id);
                logger.info("Eliminados {} detalles de pedido que referenciaban producto id={}", removedDetalles, id);
            } catch (Exception e) {
                logger.warn("No se pudieron eliminar detalles de pedido para producto id={}: {}", id, e.getMessage());
            }

            productoRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Error al eliminar producto id: " + id, e);
        }
    }

    public void deleteAll() {
        productoRepository.deleteAll();
    }


    // =======================
    // Casos de uso de catálogo
    // =======================

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorTexto(String texto) {
        try {
            if (texto == null || texto.isBlank()) {
                return findAll();
            }
            return productoRepository.findByNombreContainingIgnoreCase(texto);
        } catch (Exception e) {
            throw new ServiceException("Error al buscar productos por texto", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> filtrarPorPrecio(Double min, Double max) {
        try {
            if (min == null && max == null) {
                return findAll();
            }
            if (min == null) min = 0.0;
            if (max == null) max = Double.MAX_VALUE;
            return productoRepository.findByPrecioBetween(min, max);
        } catch (Exception e) {
            throw new ServiceException("Error al filtrar productos por precio", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> ordenarPorPrecioAsc() {
        try {
            return productoRepository.findAllByOrderByPrecioAsc();
        } catch (Exception e) {
            throw new ServiceException("Error al ordenar productos asc", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> ordenarPorPrecioDesc() {
        try {
            return productoRepository.findAllByOrderByPrecioDesc();
        } catch (Exception e) {
            throw new ServiceException("Error al ordenar productos desc", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarAvanzado(String texto,
                                         Double minPrecio,
                                         Double maxPrecio) {
        try {
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
        } catch (Exception e) {
            throw new ServiceException("Error en búsqueda avanzada de productos", e);
        }
    }

}
