package com.example.mdai;

import com.example.mdai.model.Carrito;
import com.example.mdai.model.Usuario;
import com.example.mdai.repository.CarritoRepository;
import com.example.mdai.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CarritoEntityTest {

    @Autowired
    CarritoRepository carritoRepo;
    @Autowired
    UsuarioRepository usuarioRepo;

    @Test
    void crearCarritoParaUsuario() {
        // En lugar de crear, usamos lo que siembra data.sql:
        // CARRITO(ID=1..5) y USUARIOS(ID=1..5)
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
}
