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

@DataJpaTest
class RelacionCarritoItemTest {

    @Autowired
    UsuarioRepository usuarioRepo;
    @Autowired
    CarritoRepository carritoRepo;
    @Autowired
    ProductoRepository productoRepo;
    @Autowired
    ItemCarritoRepository itemRepo;

    @Test
    void carritoContieneVariosItems() {
        Usuario u = usuarioRepo.save(new Usuario("Sara","sara@zerouno.com"));
        Carrito c = carritoRepo.save(new Carrito(u));

        Producto p1 = productoRepo.save(new Producto("Teclado", 30.0));
        Producto p2 = productoRepo.save(new Producto("Ratón", 10.0));

        itemRepo.save(new ItemCarrito(c, p1, 1, p1.getPrecio()));
        itemRepo.save(new ItemCarrito(c, p2, 2, p2.getPrecio()));

        Carrito loaded = carritoRepo.findById(c.getId()).orElseThrow();
        assertThat(loaded.getItems()).hasSize(2);
        assertThat(loaded.getTotal()).isEqualTo(30.0 + 2*10.0);
    }

    @Test
    void eliminarItemHaceRecalculoTotal() {
        Usuario u = usuarioRepo.save(new Usuario("Isa","isa@zerouno.com"));
        Carrito c = carritoRepo.save(new Carrito(u));
        Producto p = productoRepo.save(new Producto("Mousepad", 5.0));

        ItemCarrito item = itemRepo.save(new ItemCarrito(c, p, 3, p.getPrecio()));
        assertThat(c.getTotal()).isEqualTo(0.0); // si no recalculas on-the-fly

        // Recargar y calcular total a demanda
        Carrito loaded = carritoRepo.findById(c.getId()).orElseThrow();
        assertThat(loaded.getItems()).hasSize(1);

        // eliminar
        loaded.eliminarProducto(p.getId());
        carritoRepo.save(loaded);

        Carrito after = carritoRepo.findById(c.getId()).orElseThrow();
        assertThat(after.getItems()).isEmpty();
        assertThat(after.getTotal()).isEqualTo(0.0);
    }
}