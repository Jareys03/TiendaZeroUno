package com.example.mdai;

import com.example.mdai.model.Producto;
import com.example.mdai.services.ProductoService;
import com.example.mdai.services.ProductoServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ProductoServiceImpl.class)
class ProductoEntityTest {

    @Autowired
    ProductoService productoService;

    @Autowired
    EntityManager em;

    /** Sube el identity de PRODUCTO a (MAX(id)+1) para no chocar con los de data.sql */
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

        Producto p = new Producto("Impresora HP", 129.99);
        p = productoService.save(p);

        assertThat(p.getId()).isNotNull();
        assertThat(p.getNombre()).isEqualTo("Impresora HP");
        assertThat(p.getPrecio()).isEqualTo(129.99);
    }

    @Test
    void actualizarProducto_conService() {
        bumpProductoIdentity();

        Producto p = productoService.save(new Producto("Portátil viejo", 400.00));

        Producto cambios = new Producto("Portátil nuevo", 799.99);
        Producto actualizado = productoService.update(p.getId(), cambios);

        assertThat(actualizado.getId()).isEqualTo(p.getId());
        assertThat(actualizado.getNombre()).isEqualTo("Portátil nuevo");
        assertThat(actualizado.getPrecio()).isEqualTo(799.99);
    }

    @Test
    void eliminarProducto_conService() {
        bumpProductoIdentity();

        Producto p = productoService.save(new Producto("Altavoz Bluetooth", 49.99));
        Long id = p.getId();

        productoService.deleteById(id);

        assertThat(productoService.findById(id)).isNotPresent();
    }

    @Test
    void findAllProductos_conService() {
        bumpProductoIdentity();

        productoService.save(new Producto("Producto1", 10.0));
        productoService.save(new Producto("Producto2", 20.0));

        List<Producto> productos = productoService.findAll();

        assertThat(productos).hasSizeGreaterThanOrEqualTo(2);
        assertThat(productos.stream().anyMatch(p -> p.getNombre().equals("Producto1"))).isTrue();
        assertThat(productos.stream().anyMatch(p -> p.getNombre().equals("Producto2"))).isTrue();
    }
}
