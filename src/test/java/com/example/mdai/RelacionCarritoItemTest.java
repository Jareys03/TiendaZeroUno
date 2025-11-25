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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.sql.init.mode=never")
class RelacionCarritoItemTest {

    @Autowired UsuarioRepository usuarioRepo;
    @Autowired CarritoRepository carritoRepo;
    @Autowired ProductoRepository productoRepo;
    @Autowired ItemCarritoRepository itemRepo;

    @Test
    void carritoContieneVariosItems() {

        // Crear usuario + carrito
        Usuario u = usuarioRepo.save(new Usuario("Sara", "sara@zerouno.com"));
        Carrito c = new Carrito(u);

        // Crear productos con id persistido
        Producto p1 = productoRepo.save(new Producto("Teclado", 30.0));
        Producto p2 = productoRepo.save(new Producto("Raton", 10.0));

        // Usar helper: crea ItemCarrito, asigna precioUnitario = precio actual
        c.agregarProducto(p1, 1); // total 30
        c.agregarProducto(p2, 2); // total +20

        // Persistir carrito (cascade guarda los items)
        carritoRepo.saveAndFlush(c);

        // Cargar desde BD para verificar integridad
        Carrito loaded = carritoRepo.findById(c.getId()).orElseThrow();

        assertThat(loaded.getItems()).hasSize(2);
        assertThat(loaded.getTotal()).isEqualTo(30.0 + 2 * 10.0);

        // Verificar precioUnitario guardado correctamente
        assertThat(
                loaded.getItems().stream()
                        .filter(i -> i.getProducto().getNombre().equals("Raton"))
                        .findFirst()
                        .orElseThrow()
                        .getPrecioUnitario()
        ).isEqualTo(10.0);
    }

    @Test
    void eliminarItemHaceRecalculoTotal() {

        // usuario + carrito
        Usuario u = usuarioRepo.save(new Usuario("Isa", "isa@zerouno.com"));
        Carrito c = new Carrito(u);

        // producto
        Producto p = productoRepo.save(new Producto("Mousepad", 5.0));

        // Guardar item: cantidad 3 → subtotal = 15
        c.agregarProducto(p, 3);
        carritoRepo.saveAndFlush(c);

        Carrito loaded = carritoRepo.findById(c.getId()).orElseThrow();

        assertThat(loaded.getItems()).hasSize(1);
        assertThat(loaded.getTotal()).isEqualTo(3 * 5.0);

        // Usar helper eliminarProducto()
        loaded.eliminarProducto(p.getId());
        carritoRepo.saveAndFlush(loaded);

        Carrito after = carritoRepo.findById(c.getId()).orElseThrow();

        // Efecto de orphanRemoval: item desaparece de BD
        assertThat(after.getItems()).isEmpty();
        assertThat(after.getTotal()).isEqualTo(0.0);

        // comprobar que realmente no queda ningún item en la tabla
        assertThat(itemRepo.findByCarrito_Id(after.getId())).isEmpty();
    }
}
