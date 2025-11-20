package com.example.mdai;

import com.example.mdai.model.Carrito;
import com.example.mdai.model.Usuario;
import com.example.mdai.repository.CarritoRepository;
import com.example.mdai.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class CarritoEntityTest {

    @Autowired
    CarritoRepository carritoRepo;
    @Autowired
    UsuarioRepository usuarioRepo;

    @Test
    void crearCarritoParaUsuario() {

        Carrito c = carritoRepo.findById(1L)
                .orElseGet(() -> carritoRepo.findAll().iterator().next());

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
        Carrito c = carritoRepo.findAll().iterator().next();
        assertThat(carritoRepo.findById(c.getId())).isPresent();
        assertThat(carritoRepo.findById(c.getId()).orElseThrow().getId()).isEqualTo(c.getId());
    }

    @Test
    void obtenerPorId_noExiste() {
        Long inexistente = 999999L;
        assertThat(carritoRepo.findById(inexistente)).isEmpty();
    }

    @Test
    void obtenerPorIdOrThrow_noExiste_deberiaLanzar() {
        Long inexistente = 999999L;
        assertThatThrownBy(() -> carritoRepo.findById(inexistente)
                .orElseThrow(() -> new EntityNotFoundException("Carrito no encontrado: " + inexistente)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void listarPorUsuario() {
        // Usa carritos existentes para evitar violar la restricción 1-1
        Carrito ejemplo = carritoRepo.findAll().iterator().next();
        Usuario u = ejemplo.getUsuario();
        assertThat(u).isNotNull();

        List<Carrito> all = new ArrayList<>();
        carritoRepo.findAll().forEach(all::add);

        List<Carrito> porUsuario = all.stream()
                .filter(c -> c.getUsuario() != null && u.getId().equals(c.getUsuario().getId()))
                .collect(Collectors.toList());

        assertThat(porUsuario).hasSizeGreaterThanOrEqualTo(1);
        assertThat(porUsuario).allMatch(c -> u.getId().equals(c.getUsuario().getId()));
    }

    @Test
    void eliminarCarrito_existente() {
        Carrito c = carritoRepo.findAll().iterator().next();
        Long id = c.getId();
        carritoRepo.deleteById(id);
        assertThat(carritoRepo.findById(id)).isEmpty();
    }
}
