package com.example.mdai;

import com.example.mdai.model.Direccion;
import com.example.mdai.model.Usuario;
import com.example.mdai.repository.DireccionRepository;
import com.example.mdai.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RelacionUsuarioDireccionTest {

    @Autowired UsuarioRepository usuarioRepo;
    @Autowired DireccionRepository direccionRepo;

    @Test
    void usuarioConVariasDirecciones() {
        Usuario u = usuarioRepo.save(new Usuario("Marta", "marta@mdai.com"));

        Direccion d1 = new Direccion("Calle Mayor 10", "Cáceres");
        Direccion d2 = new Direccion("Avenida Sol 5", "Mérida");

        // usar helpers para mantener ambos lados sincronizados
        u.agregarDireccion(d1);
        u.agregarDireccion(d2);

        // al guardar usuario, por cascade=ALL se guardan direcciones
        usuarioRepo.save(u);

        Usuario loaded = usuarioRepo.findById(u.getId()).orElseThrow();
        assertThat(loaded.getDirecciones()).hasSize(2);
        assertThat(direccionRepo.count()).isEqualTo(2);
    }

    @Test
    void orphanRemovalEliminaDireccionAlQuitarlaDelUsuario() {
        Usuario u = usuarioRepo.save(new Usuario("Nora", "nora@mdai.com"));
        Direccion d = new Direccion("C/ Luna 7", "Cáceres");
        u.agregarDireccion(d);
        usuarioRepo.save(u);

        // quitar desde el dueño de la colección
        u = usuarioRepo.findById(u.getId()).orElseThrow();
        Direccion aBorrar = u.getDirecciones().get(0);
        u.quitarDireccion(aBorrar);
        usuarioRepo.save(u);

        assertThat(direccionRepo.findAll()).isEmpty(); // orphanRemoval=true
    }
}
