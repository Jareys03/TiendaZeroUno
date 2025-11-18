package com.example.mdai;

import com.example.mdai.model.Direccion;
import com.example.mdai.model.Usuario;
import com.example.mdai.repository.DireccionRepository;
import com.example.mdai.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DireccionEntityTest {

    @Autowired
    private DireccionRepository repo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private JdbcTemplate jdbc;

    private void bumpDireccionesIdentity() {
        Long next = jdbc.queryForObject("SELECT COALESCE(MAX(ID),0)+1 FROM DIRECCIONES", Long.class);
        jdbc.execute("ALTER TABLE DIRECCIONES ALTER COLUMN ID RESTART WITH " + next);
    }

    @Test
    void crearYLeerDireccion() {
        bumpDireccionesIdentity(); // evita PK violation

        // Usa un usuario existente del data.sql
        Usuario u = usuarioRepo.findById(1L)
                .orElseGet(() -> usuarioRepo.findFirstBycorreo("javier@example.com").orElseThrow());

        Direccion d = new Direccion("C/ Sol 123", "Badajoz");
        d.setUsuario(u); // NOT NULL: obligatorio
        d = repo.save(d);

        Direccion loaded = repo.findById(d.getId()).orElseThrow();
        assertThat(loaded.getCiudad()).isEqualTo("Badajoz");
        assertThat(loaded.getUsuario()).isNotNull();
        assertThat(loaded.getUsuario().getId()).isEqualTo(u.getId());
    }

    @Test
    void eliminarDireccion() {
        bumpDireccionesIdentity(); // evita PK violation al crear la dirección efímera

        Usuario u = usuarioRepo.findById(2L)
                .orElseGet(() -> usuarioRepo.findFirstBycorreo("laura@example.com").orElseThrow());

        Direccion d = new Direccion("C/ Río 5", "Mérida");
        d.setUsuario(u);
        d = repo.save(d);

        Long id = d.getId();
        repo.deleteById(id);

        assertThat(repo.findById(id)).isEmpty();
    }
}
