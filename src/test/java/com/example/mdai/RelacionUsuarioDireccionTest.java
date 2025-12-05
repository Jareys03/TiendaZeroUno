package com.example.mdai;

import com.example.mdai.model.Direccion;
import com.example.mdai.model.Usuario;
import com.example.mdai.repository.DireccionRepository;
import com.example.mdai.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.sql.init.mode=never")
class RelacionUsuarioDireccionTest {

    @Autowired UsuarioRepository usuarioRepo;
    @Autowired DireccionRepository direccionRepo;

    @Test
    void usuarioConVariasDirecciones() {

        // Usuario persistido con ID
        Usuario u = usuarioRepo.save(new Usuario("Marta", "marta@mdai.com"));

        // Crear direcciones sin persistir todavía
        Direccion d1 = new Direccion("Calle Mayor 10", "Cáceres");
        Direccion d2 = new Direccion("Avenida Sol 5", "Mérida");

        // Helpers de Usuario asignan el usuario a la dirección
        u.agregarDireccion(d1);
        u.agregarDireccion(d2);

        // Guardar usuario propaga las direcciones por cascade y forzar flush
        usuarioRepo.saveAndFlush(u);

        // Recargar desde BD para comprobar que la relación es real
        Usuario loaded = usuarioRepo.findById(u.getId()).orElseThrow();

        assertThat(loaded.getDirecciones()).hasSize(2);
        assertThat(direccionRepo.count()).isEqualTo(2);
    }

    @Test
    void orphanRemovalEliminaDireccionAlQuitarlaDelUsuario() {

        Usuario u = usuarioRepo.save(new Usuario("Nora", "nora@mdai.com"));

        Direccion d = new Direccion("C/ Luna 7", "Cáceres");
        u.agregarDireccion(d);

        usuarioRepo.saveAndFlush(u);

        // Recargar para asegurarnos de que la relación existe en BD
        Usuario reloaded = usuarioRepo.findById(u.getId()).orElseThrow();

        assertThat(reloaded.getDirecciones()).hasSize(1);

        // Quitar dirección con helper (la deja huérfana)
        Direccion aEliminar = reloaded.getDirecciones().get(0);
        reloaded.quitarDireccion(aEliminar);

        usuarioRepo.saveAndFlush(reloaded); // JPA borra la dirección huérfana automáticamente

        // Comprobar que ya no queda ninguna dirección en BD
        assertThat(direccionRepo.findAll()).isEmpty();
    }
}