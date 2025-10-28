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
        Usuario u = usuarioRepo.save(new Usuario("Pepe", "pepe@zerouno.com"));
        Carrito c = carritoRepo.save(new Carrito(u));

        assertThat(c.getId()).isNotNull();
        assertThat(c.getUsuario().getId()).isEqualTo(u.getId());
    }
}
