package com.example.mdai;

import com.example.mdai.model.Direccion;
import com.example.mdai.model.Usuario;
import com.example.mdai.services.DireccionService;
import com.example.mdai.services.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = "spring.sql.init.mode=never")
class DireccionEntityTest {

    @Autowired
    private DireccionService direccionService;

    @Autowired
    private UsuarioService usuarioService;

    // Helper
    private Usuario crearUsuario(String nombre, String correo) {
        Usuario u = new Usuario(nombre, correo);
        return usuarioService.registrarUsuario(u);
    }

    @Test
    void crearYLeerDireccion() {
        Usuario u = crearUsuario("Javier", "javier@example.com");

        Direccion d = new Direccion("C/ Sol 123", "Badajoz");
        usuarioService.agregarDireccion(u.getId(), d);

        // Buscar la dirección recién creada SIN usar usuario.getDirecciones()
        List<Direccion> all = direccionService.findAll();
        Direccion dirPersistida = all.stream()
                .filter(dir -> "C/ Sol 123".equals(dir.getCalle())
                        && dir.getUsuario() != null
                        && u.getId().equals(dir.getUsuario().getId()))
                .findFirst()
                .orElseThrow();

        Long idDireccion = dirPersistida.getId();

        Direccion loaded = direccionService.findById(idDireccion)
                .orElseThrow();

        assertThat(loaded.getCiudad()).isEqualTo("Badajoz");
        assertThat(loaded.getUsuario().getId()).isEqualTo(u.getId());
    }

    @Test
    void eliminarDireccion() {
        Usuario u = crearUsuario("Laura", "laura@example.com");

        Direccion d = new Direccion("C/ Río 5", "Mérida");
        usuarioService.agregarDireccion(u.getId(), d);

        List<Direccion> all = direccionService.findAll();
        Direccion dirPersistida = all.stream()
                .filter(dir -> "C/ Río 5".equals(dir.getCalle())
                        && dir.getUsuario() != null
                        && u.getId().equals(dir.getUsuario().getId()))
                .findFirst()
                .orElseThrow();

        Long id = dirPersistida.getId();

        direccionService.deleteById(id);

        assertThat(direccionService.findById(id)).isEmpty();
    }

    @Test
    void obtenerPorId_existente() {
        Usuario u = crearUsuario("Javier2", "javier2@example.com");

        Direccion d = new Direccion("C/ Olivo 7", "Cáceres");
        usuarioService.agregarDireccion(u.getId(), d);

        List<Direccion> all = direccionService.findAll();
        Direccion dirPersistida = all.stream()
                .filter(dir -> "C/ Olivo 7".equals(dir.getCalle())
                        && dir.getUsuario() != null
                        && u.getId().equals(dir.getUsuario().getId()))
                .findFirst()
                .orElseThrow();

        Long id = dirPersistida.getId();

        assertThat(direccionService.findById(id)).isPresent();
        assertThat(direccionService.findById(id).orElseThrow().getCalle())
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
        Usuario u1 = crearUsuario("JavierList", "javier.list@example.com");
        Usuario u2 = crearUsuario("LauraList", "laura.list@example.com");

        Direccion a1 = new Direccion("C/ A 1", "CiudadA");
        Direccion a2 = new Direccion("C/ A 2", "CiudadA");
        Direccion b1 = new Direccion("C/ B 1", "CiudadB");

        usuarioService.agregarDireccion(u1.getId(), a1);
        usuarioService.agregarDireccion(u1.getId(), a2);
        usuarioService.agregarDireccion(u2.getId(), b1);

        List<Direccion> all = direccionService.findAll();

        List<Direccion> porUsuario1 = all.stream()
                .filter(d -> d.getUsuario() != null &&
                        u1.getId().equals(d.getUsuario().getId()))
                .toList();

        assertThat(porUsuario1).hasSize(2);
        assertThat(porUsuario1).allMatch(d -> u1.getId().equals(d.getUsuario().getId()));
    }
}