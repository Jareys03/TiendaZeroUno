package com.example.mdai;

import com.example.mdai.model.Direccion;
import com.example.mdai.model.Usuario;
import com.example.mdai.repository.DireccionRepository;
import com.example.mdai.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.sql.init.mode=never")
class RelacionUsuarioDireccionTest {

    @Autowired UsuarioRepository usuarioRepo;
    @Autowired DireccionRepository direccionRepo;
    @Autowired TestEntityManager em; // <- para flush/clear

    @Test
    void usuarioConVariasDirecciones() {
        Usuario u = usuarioRepo.save(new Usuario("Marta", "marta@mdai.com"));

        Direccion d1 = new Direccion("Calle Mayor 10", "Caceres");
        Direccion d2 = new Direccion("Avenida Sol 5", "Merida");

        u.agregarDireccion(d1);
        u.agregarDireccion(d2);

        usuarioRepo.save(u);
        em.flush();   // fuerza INSERTs
        em.clear();   // para recargar desde la BD

        Usuario loaded = usuarioRepo.findById(u.getId()).orElseThrow();
        assertThat(loaded.getDirecciones()).hasSize(2);
        assertThat(direccionRepo.count()).isEqualTo(2L);
    }

    @Test
    void orphanRemovalEliminaDireccionAlQuitarlaDelUsuario() {
        Usuario u = usuarioRepo.save(new Usuario("Nora", "nora@mdai.com"));
        Direccion d = new Direccion("C/ Luna 7", "Caceres");
        u.agregarDireccion(d);

        usuarioRepo.save(u);
        em.flush();
        em.clear();

        Usuario reloaded = usuarioRepo.findById(u.getId()).orElseThrow();
        Direccion aBorrar = reloaded.getDirecciones().get(0);
        reloaded.quitarDireccion(aBorrar);

        usuarioRepo.save(reloaded);
        em.flush();   // provoca DELETE del huerfano
        em.clear();

        assertThat(direccionRepo.findAll()).isEmpty();
    }
}
