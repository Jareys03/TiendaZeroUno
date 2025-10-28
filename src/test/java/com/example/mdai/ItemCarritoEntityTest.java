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
class ItemCarritoEntityTest {

    @Autowired
    UsuarioRepository usuarioRepo;
    @Autowired
    CarritoRepository carritoRepo;
    @Autowired
    ProductoRepository productoRepo;
    @Autowired
    ItemCarritoRepository itemRepo;

    @Test
    void crearItemCarritoBasico() {
        Usuario u = usuarioRepo.save(new Usuario("Lola", "lola@zerouno.com"));
        Carrito c = carritoRepo.save(new Carrito(u));
        Producto p = productoRepo.save(new Producto("Auriculares", 25.0));

        ItemCarrito item = itemRepo.save(new ItemCarrito(c, p, 2, p.getPrecio()));

        assertThat(item.getId()).isNotNull();
        assertThat(item.getSubtotal()).isEqualTo(50.0);
    }
}
