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
class RelacionItemProductoTest {

    @Autowired
    ProductoRepository productoRepo;
    @Autowired
    UsuarioRepository usuarioRepo;
    @Autowired
    CarritoRepository carritoRepo;
    @Autowired
    ItemCarritoRepository itemRepo;

    @Test
    void itemReferenciaProducto() {
        Producto p = productoRepo.save(new Producto("USB-C Cable", 8.50));
        Usuario u = usuarioRepo.save(new Usuario("Rafa","rafa@zerouno.com"));
        Carrito c = carritoRepo.save(new Carrito(u));

        ItemCarrito item = itemRepo.save(new ItemCarrito(c, p, 2, p.getPrecio()));
        ItemCarrito loaded = itemRepo.findById(item.getId()).orElseThrow();

        assertThat(loaded.getProducto().getNombre()).isEqualTo("USB-C Cable");
        assertThat(loaded.getSubtotal()).isEqualTo(17.0);
    }
}
