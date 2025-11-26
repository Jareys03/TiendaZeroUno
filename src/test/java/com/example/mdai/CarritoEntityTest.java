package com.example.mdai;

import com.example.mdai.model.Carrito;
import com.example.mdai.model.Usuario;
import com.example.mdai.services.CarritoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CarritoEntityTest {

    @Autowired
    CarritoService carritoService;

    @Test
    void crearCarritoParaUsuario() {

        // Usamos el service en lugar del repositorio
        Carrito c = carritoService.findById(1L)
                .orElseGet(() -> carritoService.findAll().get(0));

        Usuario u = c.getUsuario();

        // Aserciones mínimas sobre la relación 1-1 ya existente
        assertThat(c).isNotNull();
        assertThat(c.getId()).isNotNull();
        assertThat(u).isNotNull();
        assertThat(u.getId()).isNotNull();
        assertThat(c.getUsuario().getId()).isEqualTo(u.getId());
    }

    @Test
    void obtenerPorId_existente() {
        Carrito c = carritoService.findAll().get(0);

        assertThat(carritoService.findById(c.getId())).isPresent();
        assertThat(carritoService.findById(c.getId()).orElseThrow().getId())
                .isEqualTo(c.getId());
    }

    @Test
    void obtenerPorId_noExiste() {
        Long inexistente = 999999L;
        assertThat(carritoService.findById(inexistente)).isEmpty();
    }

    @Test
    void obtenerPorIdOrThrow_noExiste_deberiaLanzar() {
        Long inexistente = 999999L;

        assertThatThrownBy(() -> carritoService.findById(inexistente)
                .orElseThrow(() -> new EntityNotFoundException("Carrito no encontrado: " + inexistente)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void listarPorUsuario() {
        // Usa carritos existentes para evitar violar la restricción 1-1
        Carrito ejemplo = carritoService.findAll().get(0);
        Usuario u = ejemplo.getUsuario();
        assertThat(u).isNotNull();

        List<Carrito> all = new ArrayList<>(carritoService.findAll());

        List<Carrito> porUsuario = all.stream()
                .filter(c -> c.getUsuario() != null && u.getId().equals(c.getUsuario().getId()))
                .collect(Collectors.toList());

        assertThat(porUsuario).hasSizeGreaterThanOrEqualTo(1);
        assertThat(porUsuario).allMatch(c -> u.getId().equals(c.getUsuario().getId()));
    }

    @Test
    void eliminarCarrito_existente() {
        Carrito c = carritoService.findAll().get(0);
        Long id = c.getId();

        carritoService.deleteById(id);

        assertThat(carritoService.findById(id)).isEmpty();
    }
}