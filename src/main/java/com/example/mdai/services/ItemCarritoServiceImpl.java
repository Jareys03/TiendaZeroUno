package com.example.mdai.services;

import com.example.mdai.exception.ResourceNotFoundException;
import com.example.mdai.exception.ServiceException;
import com.example.mdai.model.ItemCarrito;
import com.example.mdai.repository.ItemCarritoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ItemCarritoServiceImpl implements ItemCarritoService {

    private final ItemCarritoRepository itemCarritoRepository;

    public ItemCarritoServiceImpl(ItemCarritoRepository itemCarritoRepository) {
        this.itemCarritoRepository = itemCarritoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemCarrito> findAll() {
        try {
            return itemCarritoRepository.findAll();
        } catch (Exception e) {
            throw new ServiceException("Error al listar items del carrito", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ItemCarrito> findById(Long id) {
        try {
            return itemCarritoRepository.findById(id);
        } catch (Exception e) {
            throw new ServiceException("Error al buscar itemCarrito por id: " + id, e);
        }
    }

    @Override
    public ItemCarrito save(ItemCarrito itemCarrito) {
        try {
            validarItem(itemCarrito);
            return itemCarritoRepository.save(itemCarrito);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al guardar itemCarrito", e);
        }
    }

    @Override
    public ItemCarrito update(Long id, ItemCarrito itemCarrito) {
        try {
            validarItem(itemCarrito);

            return itemCarritoRepository.findById(id)
                    .map(existing -> {
                        existing.setCarrito(itemCarrito.getCarrito());
                        existing.setProducto(itemCarrito.getProducto());
                        existing.setCantidad(itemCarrito.getCantidad());
                        existing.setPrecioUnitario(itemCarrito.getPrecioUnitario());
                        return itemCarritoRepository.save(existing);
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("ItemCarrito no encontrado con id: " + id));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al actualizar itemCarrito id: " + id, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            itemCarritoRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Error al eliminar itemCarrito id: " + id, e);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemCarrito> findByCarritoId(Long carritoId) {
        try {
            return itemCarritoRepository.findByCarrito_Id(carritoId);
        } catch (Exception e) {
            throw new ServiceException("Error al buscar items por carrito id: " + carritoId, e);
        }
    }

    @Override
    public long deleteByCarritoId(Long carritoId) {
        try {
            return itemCarritoRepository.deleteByCarrito_Id(carritoId);
        } catch (Exception e) {
            throw new ServiceException("Error al eliminar items por carrito id: " + carritoId, e);
        }
    }


    private void validarItem(ItemCarrito item) {
        if (item.getCarrito() == null) {
            throw new IllegalArgumentException("El item debe estar asociado a un carrito");
        }
        if (item.getProducto() == null) {
            throw new IllegalArgumentException("El item debe estar asociado a un producto");
        }
        if (item.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad mínima es 1");
        }
        if (item.getPrecioUnitario() < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo");
        }
    }
}
