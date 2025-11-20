package com.example.mdai;

import com.example.mdai.model.Direccion;
import com.example.mdai.model.Usuario;
import com.example.mdai.repository.DireccionRepository;
import com.example.mdai.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DireccionEntityTest {

    @Autowired
    private DireccionRepository repo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private EntityManager em;

    private void bumpDireccionesIdentity() {
        Long next = ((Number) em.createNativeQuery(
                "SELECT COALESCE(MAX(ID),0)+1 FROM DIRECCIONES"
        ).getSingleResult()).longValue();

        em.createNativeQuery(
                "ALTER TABLE DIRECCIONES ALTER COLUMN ID RESTART WITH " + next
        ).executeUpdate();
    }

    @Test
    void crearYLeerDireccion() {
        bumpDireccionesIdentity();

        // Usuario que ya existe en data.sql
        Usuario u = usuarioRepo.findFirstBycorreo("javier@example.com")
                .orElseThrow();

        Direccion d = new Direccion("C/ Sol 123", "Badajoz");
        d.setUsuario(u);

        d = repo.save(d);

        Direccion loaded = repo.findById(d.getId()).orElseThrow();

        assertThat(loaded.getCiudad()).isEqualTo("Badajoz");
        assertThat(loaded.getUsuario().getId()).isEqualTo(u.getId());
    }

    @Test
    void eliminarDireccion() {
        bumpDireccionesIdentity();

        Usuario u = usuarioRepo.findFirstBycorreo("laura@example.com")
                .orElseThrow();

        Direccion d = new Direccion("C/ Río 5", "Mérida");
        d.setUsuario(u);

        d = repo.save(d);

        Long id = d.getId();
        repo.deleteById(id);

        assertThat(repo.findById(id)).isEmpty();
    }
}
