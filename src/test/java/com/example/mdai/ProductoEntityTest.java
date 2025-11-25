package com.example.mdai;

import com.example.mdai.model.Producto;
import com.example.mdai.services.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductoEntityTest {

    @Autowired
    ProductoService productoService;

    @Test
    void crearProducto_conService() {
        Producto nuevo = new Producto("Cable HDMI (test)", 39.90);
        Producto guardado = productoService.save(nuevo);

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("Cable HDMI (test)");
        assertThat(guardado.getPrecio()).isEqualTo(39.90);

        assertThat(productoService.findById(guardado.getId())).isPresent();
    }

    @Test
    void actualizarProducto_conService() {
        Producto p = productoService.save(new Producto("Teclado HP", 20.0));

        Producto cambios = new Producto("Teclado HP (modificado)", 25.5);
        Producto actualizado = productoService.update(p.getId(), cambios);

        assertThat(actualizado.getNombre()).isEqualTo("Teclado HP (modificado)");
        assertThat(actualizado.getPrecio()).isEqualTo(25.5);
    }

    @Test
    void eliminarProducto_conService() {
        Producto p = productoService.save(new Producto("Producto temporal", 10.0));
        Long id = p.getId();

        productoService.deleteById(id);

        assertThat(productoService.findById(id)).isEmpty();
    }

    @Test
    void findAllProductos_conService() {
        productoService.save(new Producto("Prod1", 5.0));
        productoService.save(new Producto("Prod2", 6.0));

        List<Producto> todos = productoService.findAll();

        assertThat(todos.size()).isGreaterThan(1);
        assertThat(todos.stream().anyMatch(p -> p.getNombre().equals("Prod1"))).isTrue();
    }
}
