package com.example.mdai;

import com.example.mdai.model.Producto;
import com.example.mdai.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductoEntityTest {

    @Autowired
    ProductoRepository repo;

    @Autowired
    JdbcTemplate jdbc;

    private void bumpProductoIdentity() {
        Long next = jdbc.queryForObject("SELECT COALESCE(MAX(ID),0)+1 FROM PRODUCTO", Long.class);
        jdbc.execute("ALTER TABLE PRODUCTO ALTER COLUMN ID RESTART WITH " + next);
    }

    @Test
    void crearProductoBasico() {
        bumpProductoIdentity(); // evita PK violation al insertar

        Producto p = repo.save(new Producto("Cable HDMI (test)", 39.90));
        assertThat(p.getId()).isNotNull();
        assertThat(repo.findById(p.getId())).isPresent();
        assertThat(repo.findById(p.getId()).get().getPrecio()).isEqualTo(39.90);
    }

    @Test
    void actualizarPrecio() {
        // No insertamos; reutilizamos un producto sembrado por data.sql, por ejemplo ID=2 (Monitor Samsung)
        Producto p = repo.findById(2L).orElseThrow();
        p.setPrecio(12.5);           // cambiamos el precio
        repo.save(p);                // genera UPDATE

        assertThat(repo.findById(2L).orElseThrow().getPrecio()).isEqualTo(12.5);
    }
}
