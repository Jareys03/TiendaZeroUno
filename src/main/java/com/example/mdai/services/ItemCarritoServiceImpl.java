package com.example.mdai.services;

import com.example.mdai.model.ItemCarrito;
import com.example.mdai.repository.ItemCarritoRepository;
import com.example.mdai.services.ItemCarritoService;
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
        return itemCarritoRepository.save(itemCarrito);
    }


    @Override
    public ItemCarrito update(Long id, ItemCarrito itemCarrito) {
        return itemCarritoRepository.findById(id)
                .map(existing -> {
                    existing.setProducto(itemCarrito.getProducto());
                    existing.setCantidad(itemCarrito.getCantidad());
                    existing.setPrecioUnitario(itemCarrito.getPrecioUnitario());
                    return itemCarritoRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Item de carrito no encontrado con id: " + id));
    }


    @Override
    public void deleteById(Long id) {
        itemCarritoRepository.deleteById(id);
    }
}
