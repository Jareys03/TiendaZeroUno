package com.example.mdai;

import com.example.mdai.model.Usuario;
import com.example.mdai.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UsuarioEntityTest {

    @Autowired
    UsuarioRepository repo;

    @Test
    void crearYLeerUsuario() {
        Usuario u = new Usuario("Laura", "laura@zerouno.com");
        u = repo.save(u);

        assertThat(u.getId()).isNotNull();
        assertThat(repo.findById(u.getId())).isPresent();
        assertThat(repo.findById(u.getId()).get().getCorreo()).isEqualTo("laura@zerouno.com");
    }

    @Test
    void actualizarUsuario() {
        Usuario u = repo.save(new Usuario("Javier", "javier@zerouno.com"));
        u.setNombre("Javi");
        repo.save(u);

        Usuario loaded = repo.findById(u.getId()).orElseThrow();
        assertThat(loaded.getNombre()).isEqualTo("Javi");
    }

    @Test
    void eliminarUsuario() {
        Usuario u = repo.save(new Usuario("Ana", "ana@zerouno.com"));
        Long id = u.getId();
        repo.deleteById(id);

        assertThat(repo.findById(id)).isNotPresent();
    }
}
