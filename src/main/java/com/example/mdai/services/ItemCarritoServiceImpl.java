package com.example.mdai.services;

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
        return itemCarritoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ItemCarrito> findById(Long id) {
        return itemCarritoRepository.findById(id);
    }

    @Override
    public ItemCarrito save(ItemCarrito itemCarrito) {
        validarItem(itemCarrito);
        return itemCarritoRepository.save(itemCarrito);
    }

    @Override
    public ItemCarrito update(Long id, ItemCarrito itemCarrito) {
        validarItem(itemCarrito);

        return itemCarritoRepository.findById(id)
                .map(existing -> {
                    existing.setCarrito(itemCarrito.getCarrito());
                    existing.setProducto(itemCarrito.getProducto());
                    existing.setCantidad(itemCarrito.getCantidad());
                    existing.setPrecioUnitario(itemCarrito.getPrecioUnitario());
                    return itemCarritoRepository.save(existing);
                })
                .orElseThrow(() ->
                        new IllegalArgumentException("ItemCarrito no encontrado con id: " + id));
    }

    @Override
    public void deleteById(Long id) {
        itemCarritoRepository.deleteById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemCarrito> findByCarritoId(Long carritoId) {
        return itemCarritoRepository.findByCarrito_Id(carritoId);
    }

    @Override
    public long deleteByCarritoId(Long carritoId) {
        return itemCarritoRepository.deleteByCarrito_Id(carritoId);
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
