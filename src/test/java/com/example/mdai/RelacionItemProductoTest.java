package com.example.mdai;

import com.example.mdai.model.Carrito;
import com.example.mdai.model.ItemCarrito;
import com.example.mdai.model.Producto;
import com.example.mdai.model.Usuario;
import com.example.mdai.repository.CarritoRepository;
import com.example.mdai.repository.ItemCarritoRepository;
import com.example.mdai.repository.ProductoRepository;
import com.example.mdai.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.sql.init.mode=never")
class RelacionItemProductoTest {

    @Autowired ProductoRepository productoRepo;
    @Autowired UsuarioRepository usuarioRepo;
    @Autowired CarritoRepository carritoRepo;
    @Autowired ItemCarritoRepository itemRepo;

    @Test
    void itemReferenciaProducto() {

        // Crear producto con id persistido
        Producto p = productoRepo.save(new Producto("USB-C Cable", 8.50));

        // Crear usuario + carrito
        Usuario u = usuarioRepo.save(new Usuario("Rafa", "rafa@zerouno.com"));
        Carrito c = carritoRepo.save(new Carrito(u));

        // Crear item a mano (2 unidades → subtotal 17.0)
        ItemCarrito item = itemRepo.save(
                new ItemCarrito(c, p, 2, p.getPrecio())
        );

        // Cargar desde BD para verificar que la relación funciona
        ItemCarrito loaded = itemRepo.findById(item.getId()).orElseThrow();

        // --- ASERCIONES ---
        // Relación Item → Producto
        assertThat(loaded.getProducto()).isNotNull();
        assertThat(loaded.getProducto().getId()).isEqualTo(p.getId());
        assertThat(loaded.getProducto().getNombre()).isEqualTo("USB-C Cable");

        // Subtotal = cantidad × precioUnitario
        assertThat(loaded.getSubtotal()).isEqualTo(17.0);

        // El precioUnitario debe quedar guardado incluso si cambia el precio del producto
        assertThat(loaded.getPrecioUnitario()).isEqualTo(8.50);

        // Relación Item → Carrito
        assertThat(loaded.getCarrito()).isNotNull();
        assertThat(loaded.getCarrito().getId()).isEqualTo(c.getId());
    }
}
