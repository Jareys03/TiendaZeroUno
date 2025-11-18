package com.example.mdai.services;

import com.example.mdai.model.Direccion;
import com.example.mdai.repository.DireccionRepository;
import com.example.mdai.services.DireccionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class DireccionServiceImpl implements DireccionService {


    private final DireccionRepository direccionRepository;


    public DireccionServiceImpl(DireccionRepository direccionRepository) {
        this.direccionRepository = direccionRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Direccion> findAll() {
        return direccionRepository.findAll();
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Direccion> findById(Long id) {
        return direccionRepository.findById(id);
    }


    @Override
    public Direccion save(Direccion direccion) {
        return direccionRepository.save(direccion);
    }


    @Override
    public Direccion update(Long id, Direccion direccion) {
        return direccionRepository.findById(id)
                .map(existing -> {
                    existing.setCalle(direccion.getCalle());
                    existing.setCiudad(direccion.getCiudad());
                    existing.setCodigoPostal(direccion.getCodigoPostal());
                    existing.setProvincia(direccion.getProvincia());
                    existing.setPais(direccion.getPais());
                    return direccionRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada con id: " + id));
    }


    @Override
    public void deleteById(Long id) {
        direccionRepository.deleteById(id);
    }
}
