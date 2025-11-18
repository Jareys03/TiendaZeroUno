package com.example.mdai.services;

import com.example.mdai.model.Pedido;
import com.example.mdai.repository.PedidoRepository;
import com.example.mdai.services.PedidoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class PedidoServiceImpl implements PedidoService {


    private final PedidoRepository pedidoRepository;


    public PedidoServiceImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }


    @Override
    public Pedido save(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }


    @Override
    public Pedido update(Long id, Pedido pedido) {
        return pedidoRepository.findById(id)
                .map(existing -> {
                    existing.setNumero(pedido.getNumero());
                    existing.setTotal(pedido.getTotal());
                    return pedidoRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con id: " + id));
    }


    @Override
    public void deleteById(Long id) {
        pedidoRepository.deleteById(id);
    }
}