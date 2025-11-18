package com.example.mdai.services;

import com.example.mdai.model.Carrito;
import com.example.mdai.repository.CarritoRepository;
import com.example.mdai.services.CarritoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {


    private final CarritoRepository carritoRepository;


    public CarritoServiceImpl(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Carrito> findAll() {
        return carritoRepository.findAll();
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Carrito> findById(Long id) {
        return carritoRepository.findById(id);
    }


    @Override
    public Carrito save(Carrito carrito) {
        return carritoRepository.save(carrito);
    }


    @Override
    public Carrito update(Long id, Carrito carrito) {
        return carritoRepository.findById(id)
                .map(existing -> {
                    existing.setUsuario(carrito.getUsuario());
                    existing.setItems(carrito.getItems());
                    return carritoRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado con id: " + id));
    }


    @Override
    public void deleteById(Long id) {
        carritoRepository.deleteById(id);
    }
}
