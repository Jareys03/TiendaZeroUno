package com.example.mdai;

import com.example.mdai.model.Direccion;
import com.example.mdai.repository.DireccionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DireccionEntityTest {

    @Autowired
    private DireccionRepository repo;

    @Test
    void crearYLeerDireccion() {
        Direccion d = new Direccion("Calle Mayor 10", "Cáceres");
        d = repo.save(d);

        assertThat(d.getId()).isNotNull();

        Direccion cargada = repo.findById(d.getId()).orElseThrow();
        assertThat(cargada.getCalle()).isEqualTo("Calle Mayor 10");
        assertThat(cargada.getCiudad()).isEqualTo("Cáceres");
    }

    @Test
    void actualizarDireccion() {
        Direccion d = repo.save(new Direccion("Avenida Sol", "Mérida"));
        d.setCiudad("Badajoz");
        repo.save(d);

        Direccion loaded = repo.findById(d.getId()).orElseThrow();
        assertThat(loaded.getCiudad()).isEqualTo("Badajoz");
    }

    @Test
    void eliminarDireccion() {
        Direccion d = repo.save(new Direccion("C/ Luna", "Trujillo"));
        Long id = d.getId();

        repo.deleteById(id);

        assertThat(repo.findById(id)).isEmpty();
    }
}
