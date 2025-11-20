package com.example.mdai;

import com.example.mdai.model.Usuario;
import com.example.mdai.services.UsuarioService;
import com.example.mdai.services.UsuarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.sql.init.mode=never")
@Import(UsuarioServiceImpl.class)
class UsuarioEntityTest {

    @Autowired
    UsuarioService usuarioService;   // usamos la interfaz del servicio

    @Test
    void crearYLeerUsuario_conService() {
        Usuario u = new Usuario("Laura", "laura@zerouno.com");
        u = usuarioService.save(u);

        assertThat(u.getId()).isNotNull();
        assertThat(usuarioService.findById(u.getId())).isPresent();
        assertThat(usuarioService.findById(u.getId()).get().getCorreo())
                .isEqualTo("laura@zerouno.com");
    }

    @Test
    void actualizarUsuario_conService() {
        Usuario u = usuarioService.save(new Usuario("Javier", "javier@zerouno.com"));

        Usuario cambios = new Usuario("Javi", "javi@zerouno.com");
        Usuario actualizado = usuarioService.update(u.getId(), cambios);

        assertThat(actualizado.getId()).isEqualTo(u.getId());
        assertThat(actualizado.getNombre()).isEqualTo("Javi");
        assertThat(actualizado.getCorreo()).isEqualTo("javi@zerouno.com");
    }

    @Test
    void eliminarUsuario_conService() {
        Usuario u = usuarioService.save(new Usuario("Ana", "ana@zerouno.com"));
        Long id = u.getId();

        usuarioService.deleteById(id);

        assertThat(usuarioService.findById(id)).isNotPresent();
    }

    @Test
    void findAllUsuarios_conService() {
        usuarioService.save(new Usuario("User1", "user1@zerouno.com"));
        usuarioService.save(new Usuario("User2", "user2@zerouno.com"));

        List<Usuario> todos = usuarioService.findAll();

        assertThat(todos).hasSizeGreaterThanOrEqualTo(2);
        assertThat(todos.stream().anyMatch(u -> u.getCorreo().equals("user1@zerouno.com")))
                .isTrue();
        assertThat(todos.stream().anyMatch(u -> u.getCorreo().equals("user2@zerouno.com")))
                .isTrue();
    }
}