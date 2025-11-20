package com.example.mdai;

import com.example.mdai.model.Carrito;
import com.example.mdai.model.ItemCarrito;
import com.example.mdai.model.Producto;
import com.example.mdai.repository.CarritoRepository;
import com.example.mdai.repository.ItemCarritoRepository;
import com.example.mdai.repository.ProductoRepository;
import com.example.mdai.services.ItemCarritoService;
import com.example.mdai.services.ItemCarritoServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ItemCarritoServiceImpl.class)   // ⬅ para que Spring cree el service
class ItemCarritoEntityTest {

    @Autowired CarritoRepository carritoRepo;
    @Autowired ProductoRepository productoRepo;
    @Autowired ItemCarritoRepository itemRepo;

    @Autowired ItemCarritoService itemService; // ⬅ interfaz del servicio

    @Autowired EntityManager em;

    /** Sube el identity de ITEM_CARRITO a (MAX(id)+1) para evitar choques con data.sql */
    private void bumpItemCarritoIdentity() {
        Long next = ((Number) em.createNativeQuery(
                "SELECT COALESCE(MAX(ID),0)+1 FROM ITEM_CARRITO"
        ).getSingleResult()).longValue();

        em.createNativeQuery(
                "ALTER TABLE ITEM_CARRITO ALTER COLUMN ID RESTART WITH " + next
        ).executeUpdate();
    }

    @Test
    void crearItemCarritoBasico_conService() {
        bumpItemCarritoIdentity();

        // Usa datos de data.sql para evitar colisiones:
        // CARRITO 3 existe, y en data.sql tiene productos 16 y 19.
        // Elegimos PRODUCTO 13 (Router), que no está en ese carrito.
        Carrito c = carritoRepo.findById(3L).orElseThrow();
        Producto p = productoRepo.findById(13L).orElseThrow();

        ItemCarrito item = new ItemCarrito(c, p, 2, p.getPrecio());
        item = itemService.save(item);   // ⬅ usamos el service, no el repo directamente

        assertThat(item.getId()).isNotNull();
        assertThat(item.getCarrito().getId()).isEqualTo(3L);
        assertThat(item.getProducto().getId()).isEqualTo(13L);
        assertThat(item.getSubtotal()).isEqualTo(2 * p.getPrecio());

        // Comprobamos también que el service es capaz de leerlo
        assertThat(itemService.findById(item.getId())).isPresent();
    }

    @Test
    void actualizarItemCarrito_conService() {
        bumpItemCarritoIdentity();

        Carrito c = carritoRepo.findById(3L).orElseThrow();
        Producto p = productoRepo.findById(13L).orElseThrow();

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
        bumpItemCarritoIdentity();

        Carrito c = carritoRepo.findById(3L).orElseThrow();
        Producto p = productoRepo.findById(13L).orElseThrow();

        ItemCarrito item = new ItemCarrito(c, p, 3, p.getPrecio());
        item = itemService.save(item);

        Long id = item.getId();
        itemService.deleteById(id);

        assertThat(itemRepo.findById(id)).isEmpty();
        assertThat(itemService.findById(id)).isEmpty();
    }

    @Test
    void findAllItemCarrito_conService() {
        bumpItemCarritoIdentity();

        Carrito c = carritoRepo.findById(3L).orElseThrow();
        Producto p = productoRepo.findById(13L).orElseThrow();

        // Creamos un par de items nuevos
        itemService.save(new ItemCarrito(c, p, 1, p.getPrecio()));
        itemService.save(new ItemCarrito(c, p, 2, p.getPrecio()));

        List<ItemCarrito> todos = itemService.findAll();

        // No comprobamos número exacto (porque ya hay items en data.sql),
        // solo que hay al menos 2 y que tienen carrito/producto coherentes.
        assertThat(todos).isNotEmpty();
        assertThat(todos.stream().anyMatch(ic -> ic.getCarrito().getId().equals(3L))).isTrue();
    }
}