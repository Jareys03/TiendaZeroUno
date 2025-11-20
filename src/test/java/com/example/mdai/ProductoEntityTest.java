package com.example.mdai;

import com.example.mdai.model.Producto;
import com.example.mdai.repository.ProductoRepository;
import com.example.mdai.services.ProductoService;
import com.example.mdai.services.ProductoServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ProductoServiceImpl.class)
class ProductoEntityTest {

    @Autowired ProductoRepository repo;
    @Autowired ProductoService productoService;
    @Autowired EntityManager em;

    private void bumpProductoIdentity() {
        Long next = ((Number) em.createNativeQuery(
                "SELECT COALESCE(MAX(ID),0)+1 FROM PRODUCTO"
        ).getSingleResult()).longValue();

        em.createNativeQuery(
                "ALTER TABLE PRODUCTO ALTER COLUMN ID RESTART WITH " + next
        ).executeUpdate();
    }

    @Test
    void crearProducto_conService() {
        bumpProductoIdentity();

        Producto nuevo = new Producto("Cable HDMI (test)", 39.90);
        Producto guardado = productoService.save(nuevo);

        assertThat(guardado.getId()).isNotNull();
        assertThat(productoService.findById(guardado.getId())).isPresent();
        assertThat(guardado.getNombre()).isEqualTo("Cable HDMI (test)");
        assertThat(guardado.getPrecio()).isEqualTo(39.90);
    }

    @Test
    void actualizarProducto_conService() {
        bumpProductoIdentity();

        // Creamos un producto para modificar después
        Producto p = productoService.save(new Producto("Teclado HP", 20.0));

        Producto cambios = new Producto("Teclado HP (modificado)", 25.5);
        Producto actualizado = productoService.update(p.getId(), cambios);

        assertThat(actualizado.getNombre()).isEqualTo("Teclado HP (modificado)");
        assertThat(actualizado.getPrecio()).isEqualTo(25.5);
    }

    @Test
    void eliminarProducto_conService() {
        bumpProductoIdentity();

        Producto p = productoService.save(new Producto("Producto temporal", 10.0));
        Long id = p.getId();

        productoService.deleteById(id);

        assertThat(productoService.findById(id)).isEmpty();
    }

    @Test
    void findAllProductos_conService() {
        bumpProductoIdentity();

        productoService.save(new Producto("Prod1", 5.0));
        productoService.save(new Producto("Prod2", 6.0));

        List<Producto> todos = productoService.findAll();

        // Como data.sql tiene muchos productos, solo comprobamos que hay más de uno
        assertThat(todos.size()).isGreaterThan(1);
        assertThat(todos.stream().anyMatch(p -> p.getNombre().equals("Prod1"))).isTrue();
    }
}