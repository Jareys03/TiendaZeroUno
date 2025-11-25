package com.example.mdai;

import com.example.mdai.model.Carrito;
import com.example.mdai.model.ItemCarrito;
import com.example.mdai.model.Producto;
import com.example.mdai.services.CarritoService;
import com.example.mdai.services.ItemCarritoService;
import com.example.mdai.services.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ItemCarritoEntityTest {

    @Autowired
    CarritoService carritoService;

    @Autowired
    ProductoService productoService;

    @Autowired
    ItemCarritoService itemService;

    @Test
    void crearItemCarritoBasico_conService() {
        // Usa datos de data.sql para evitar colisiones:
        // Se asume que CARRITO 3 y PRODUCTO 13 existen en data.sql
        Carrito c = carritoService.findById(3L).orElseThrow();
        Producto p = productoService.findById(13L).orElseThrow();

        ItemCarrito item = new ItemCarrito(c, p, 2, p.getPrecio());
        item = itemService.save(item);   // usamos el service, no el repo directamente

        assertThat(item.getId()).isNotNull();
        assertThat(item.getCarrito().getId()).isEqualTo(3L);
        assertThat(item.getProducto().getId()).isEqualTo(13L);
        assertThat(item.getSubtotal()).isEqualTo(2 * p.getPrecio());

        // Comprobamos también que el service es capaz de leerlo
        assertThat(itemService.findById(item.getId())).isPresent();
    }

    @Test
    void actualizarItemCarrito_conService() {
        Carrito c = carritoService.findById(3L).orElseThrow();
        Producto p = productoService.findById(13L).orElseThrow();

        // Creamos primero un ítem
        ItemCarrito item = new ItemCarrito(c, p, 1, p.getPrecio());
        item = itemService.save(item);

        // Preparamos datos nuevos para el update
        ItemCarrito cambios = new ItemCarrito(c, p, 5, p.getPrecio() + 10.0);

        ItemCarrito actualizado = itemService.update(item.getId(), cambios);

        assertThat(actualizado.getId()).isEqualTo(item.getId());
        assertThat(actualizado.getCantidad()).isEqualTo(5);
        assertThat(actualizado.getPrecioUnitario()).isEqualTo(p.getPrecio() + 10.0);
        assertThat(actualizado.getSubtotal()).isEqualTo(5 * (p.getPrecio() + 10.0));
    }

    @Test
    void eliminarItemCarrito_conService() {
        Carrito c = carritoService.findById(3L).orElseThrow();
        Producto p = productoService.findById(13L).orElseThrow();

        ItemCarrito item = new ItemCarrito(c, p, 3, p.getPrecio());
        item = itemService.save(item);

        Long id = item.getId();
        itemService.deleteById(id);

        assertThat(itemService.findById(id)).isEmpty();
    }

    @Test
    void findAllItemCarrito_conService() {
        Carrito c = carritoService.findById(3L).orElseThrow();
        Producto p = productoService.findById(13L).orElseThrow();

        // Creamos un par de items nuevos
        itemService.save(new ItemCarrito(c, p, 1, p.getPrecio()));
        itemService.save(new ItemCarrito(c, p, 2, p.getPrecio()));

        List<ItemCarrito> todos = itemService.findAll();

        // No comprobamos número exacto (porque ya hay items en data.sql),
        // solo que hay al menos 1 y que hay alguno con carrito 3.
        assertThat(todos).isNotEmpty();
        assertThat(todos.stream().anyMatch(ic -> ic.getCarrito().getId().equals(3L))).isTrue();
    }
}
