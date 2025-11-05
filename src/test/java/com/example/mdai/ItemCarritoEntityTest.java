package com.example.mdai;

import com.example.mdai.model.Carrito;
import com.example.mdai.model.ItemCarrito;
import com.example.mdai.model.Producto;
import com.example.mdai.repository.CarritoRepository;
import com.example.mdai.repository.ItemCarritoRepository;
import com.example.mdai.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemCarritoEntityTest {

    @Autowired CarritoRepository carritoRepo;
    @Autowired ProductoRepository productoRepo;
    @Autowired ItemCarritoRepository itemRepo;

    @Test
    void crearItemCarritoBasico() {
        // Usa datos de data.sql para evitar colisiones:
        // CARRITO 3 existe, y en data.sql tiene productos 16 y 19.
        // Elegimos PRODUCTO 13 (Router), que no está en ese carrito.
        Carrito c = carritoRepo.findById(3L).orElseThrow();
        Producto p = productoRepo.findById(13L).orElseThrow();

        ItemCarrito item = new ItemCarrito(c, p, 2, p.getPrecio());
        item = itemRepo.save(item);

        assertThat(item.getId()).isNotNull();
        assertThat(item.getCarrito().getId()).isEqualTo(3L);
        assertThat(item.getProducto().getId()).isEqualTo(13L);
        assertThat(item.getSubtotal()).isEqualTo(2 * p.getPrecio());
    }
}
