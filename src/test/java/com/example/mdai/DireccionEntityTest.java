package com.example.mdai;

import com.example.mdai.model.Direccion;
import com.example.mdai.model.Usuario;
import com.example.mdai.services.DireccionService;
import com.example.mdai.services.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class DireccionEntityTest {

    @Autowired
    private DireccionService direccionService;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    void crearYLeerDireccion() {
        // Usuario de prueba (ya existente en data.sql)
        Usuario u = usuarioService.buscarPorCorreo("javier@example.com")
                .orElseThrow();

        Direccion d = new Direccion("C/ Sol 123", "Badajoz");

        // CASO DE USO → agregar dirección al usuario
        usuarioService.agregarDireccion(u.getId(), d);

        Long idDireccion = d.getId();

        Direccion loaded = direccionService.findById(idDireccion)
                .orElseThrow();

        assertThat(loaded.getCiudad()).isEqualTo("Badajoz");
        assertThat(loaded.getUsuario().getId()).isEqualTo(u.getId());
    }

    @Test
    void eliminarDireccion() {
        Usuario u = usuarioService.buscarPorCorreo("laura@example.com")
                .orElseThrow();

        Direccion d = new Direccion("C/ Río 5", "Mérida");
        usuarioService.agregarDireccion(u.getId(), d);

        Long id = d.getId();

        direccionService.deleteById(id);

        assertThat(direccionService.findById(id)).isEmpty();
    }

    @Test
    void obtenerPorId_existente() {
        Usuario u = usuarioService.buscarPorCorreo("javier@example.com")
                .orElseThrow();

        Direccion d = new Direccion("C/ Olivo 7", "Cáceres");
        usuarioService.agregarDireccion(u.getId(), d);

        assertThat(direccionService.findById(d.getId())).isPresent();
        assertThat(direccionService.findById(d.getId()).orElseThrow().getCalle())
                .isEqualTo("C/ Olivo 7");
    }

    @Test
    void obtenerPorId_noExiste() {
        Long inexistente = 999999L;
        assertThat(direccionService.findById(inexistente)).isEmpty();
    }

    @Test
    void obtenerPorIdOrThrow_noExiste_deberiaLanzar() {
        Long inexistente = 999999L;

        assertThatThrownBy(() ->
                direccionService.findById(inexistente)
                        .orElseThrow(() -> new EntityNotFoundException("Direccion no encontrada: " + inexistente))
        ).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void listarPorUsuario() {
        Usuario u1 = usuarioService.buscarPorCorreo("javier@example.com")
                .orElseThrow();
        Usuario u2 = usuarioService.buscarPorCorreo("laura@example.com")
                .orElseThrow();

        Direccion a1 = new Direccion("C/ A 1", "CiudadA");
        Direccion a2 = new Direccion("C/ A 2", "CiudadA");
        Direccion b1 = new Direccion("C/ B 1", "CiudadB");

        usuarioService.agregarDireccion(u1.getId(), a1);
        usuarioService.agregarDireccion(u1.getId(), a2);
        usuarioService.agregarDireccion(u2.getId(), b1);

        // Obtener TODAS las direcciones desde el servicio
        List<Direccion> all = direccionService.findAll();

        List<Direccion> porUsuario1 = all.stream()
                .filter(d -> d.getUsuario() != null &&
                        u1.getId().equals(d.getUsuario().getId()))
                .toList();

        assertThat(porUsuario1).hasSizeGreaterThanOrEqualTo(2);
        assertThat(porUsuario1).allMatch(d -> u1.getId().equals(d.getUsuario().getId()));
    }
}
