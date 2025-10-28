package com.example.mdai;

import com.example.mdai.model.Producto;
import com.example.mdai.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductoEntityTest {

    @Autowired
    ProductoRepository repo;

    @Test
    void crearProductoBasico() {
        Producto p = repo.save(new Producto("Teclado", 39.90));
        assertThat(p.getId()).isNotNull();
        assertThat(repo.findById(p.getId())).isPresent();
        assertThat(repo.findById(p.getId()).get().getPrecio()).isEqualTo(39.90);
    }

    @Test
    void actualizarPrecio() {
        Producto p = repo.save(new Producto("Ratón", 10.0));
        p.setPrecio(12.5);
        repo.save(p);

        assertThat(repo.findById(p.getId()).orElseThrow().getPrecio()).isEqualTo(12.5);
    }
}
