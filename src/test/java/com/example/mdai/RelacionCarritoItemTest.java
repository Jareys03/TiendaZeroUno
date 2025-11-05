package com.example.mdai;

import com.example.mdai.model.Carrito;
import com.example.mdai.model.Producto;
import com.example.mdai.model.Usuario;
import com.example.mdai.repository.CarritoRepository;
import com.example.mdai.repository.ItemCarritoRepository;
import com.example.mdai.repository.ProductoRepository;
import com.example.mdai.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(properties = "spring.sql.init.mode=never")
class RelacionCarritoItemTest {

    @Autowired UsuarioRepository usuarioRepo;
    @Autowired CarritoRepository carritoRepo;
    @Autowired ProductoRepository productoRepo;
    @Autowired ItemCarritoRepository itemRepo; // ok tenerlo inyectado

    @Test
    void carritoContieneVariosItems() {
        // usuario y carrito
        Usuario u = usuarioRepo.save(new Usuario("Sara", "sara@zerouno.com"));
        Carrito c = new Carrito(u);

        // productos (guardarlos antes para que tengan id)
        Producto p1 = productoRepo.save(new Producto("Teclado", 30.0));
        Producto p2 = productoRepo.save(new Producto("Raton", 10.0));

        // helpers de Carrito mantienen ambos lados
        c.agregarProducto(p1, 1);
        c.agregarProducto(p2, 2);

        // persistir el padre (cascade guarda los items)
        carritoRepo.saveAndFlush(c);

        // comprobar
        Carrito loaded = carritoRepo.findById(c.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(loaded.getItems()).hasSize(2);
        org.assertj.core.api.Assertions.assertThat(loaded.getTotal()).isEqualTo(30.0 + 2 * 10.0);
    }

    @Test
    void eliminarItemHaceRecalculoTotal() {
        // usuario y carrito
        Usuario u = usuarioRepo.save(new Usuario("Isa", "isa@zerouno.com"));
        Carrito c = new Carrito(u);

        // producto
        Producto p = productoRepo.save(new Producto("Mousepad", 5.0));

        // agregar 3 unidades
        c.agregarProducto(p, 3);
        carritoRepo.saveAndFlush(c);

        // con tu getTotal() (suma dinamica) debe dar 15.0
        org.assertj.core.api.Assertions.assertThat(c.getTotal()).isEqualTo(15.0);

        Carrito loaded = carritoRepo.findById(c.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(loaded.getItems()).hasSize(1);

        // eliminar por helper; orphanRemoval borra el hijo al guardar
        loaded.eliminarProducto(p.getId());
        carritoRepo.saveAndFlush(loaded);

        Carrito after = carritoRepo.findById(c.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(after.getItems()).isEmpty();
        org.assertj.core.api.Assertions.assertThat(after.getTotal()).isEqualTo(0.0);
    }
}
