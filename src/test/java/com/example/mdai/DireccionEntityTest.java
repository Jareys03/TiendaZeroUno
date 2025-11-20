package com.example.mdai;

import com.example.mdai.model.Direccion;
import com.example.mdai.model.Usuario;
import com.example.mdai.repository.DireccionRepository;
import com.example.mdai.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.stream.StreamSupport;


import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void obtenerPorId_existente() {
        bumpDireccionesIdentity();

        Usuario u = usuarioRepo.findFirstBycorreo("javier@example.com")
                .orElseThrow();

        Direccion d = new Direccion("C/ Olivo 7", "Cáceres");
        d.setUsuario(u);
        d = repo.save(d);

        assertThat(repo.findById(d.getId())).isPresent();
        assertThat(repo.findById(d.getId()).orElseThrow().getCalle()).isEqualTo("C/ Olivo 7");
    }

    @Test
    void obtenerPorId_noExiste() {
        // id improbable que no exista en pruebas
        Long inexistente = 999999L;
        assertThat(repo.findById(inexistente)).isEmpty();
    }

    @Test
    void obtenerPorIdOrThrow_noExiste_deberiaLanzar() {
        Long inexistente = 999999L;
        assertThatThrownBy(() -> repo.findById(inexistente)
                .orElseThrow(() -> new EntityNotFoundException("Direccion no encontrada: " + inexistente)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void listarPorUsuario() {
        bumpDireccionesIdentity();

        Usuario u1 = usuarioRepo.findFirstBycorreo("javier@example.com")
                .orElseThrow();
        Usuario u2 = usuarioRepo.findFirstBycorreo("laura@example.com")
                .orElseThrow();

        Direccion a1 = new Direccion("C/ A 1", "CiudadA");
        a1.setUsuario(u1);
        Direccion a2 = new Direccion("C/ A 2", "CiudadA");
        a2.setUsuario(u1);
        Direccion b1 = new Direccion("C/ B 1", "CiudadB");
        b1.setUsuario(u2);

        repo.save(a1);
        repo.save(a2);
        repo.save(b1);

        // Copiar el Iterable a una List para preservar el tipo Direccion
        java.util.List<Direccion> all = new java.util.ArrayList<>();
        repo.findAll().forEach(all::add);

        java.util.List<Direccion> porUsuario1 = all.stream()
                .filter(d -> d.getUsuario() != null && u1.getId().equals(d.getUsuario().getId()))
                .collect(java.util.stream.Collectors.toList());

        assertThat(porUsuario1).hasSizeGreaterThanOrEqualTo(2);
        assertThat(porUsuario1).allMatch(d -> u1.getId().equals(d.getUsuario().getId()));
    }
}