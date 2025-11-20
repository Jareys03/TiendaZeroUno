package com.example.mdai;

import com.example.mdai.model.Producto;
import com.example.mdai.repository.ProductoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductoEntityTest {

    @Autowired
    ProductoRepository repo;

    @Autowired
    EntityManager em;

    private void bumpProductoIdentity() {
        Long next = ((Number) em.createNativeQuery(
                "SELECT COALESCE(MAX(ID),0)+1 FROM PRODUCTO"
        ).getSingleResult()).longValue();

        em.createNativeQuery(
                "ALTER TABLE PRODUCTO ALTER COLUMN ID RESTART WITH " + next
        ).executeUpdate();
    }

    @Test
    void crearProductoBasico() {
        bumpProductoIdentity();
        Producto p = repo.save(new Producto("Cable HDMI (test)", 39.90));

        assertThat(p.getId()).isNotNull();
        assertThat(repo.findById(p.getId())).isPresent();
        assertThat(repo.findById(p.getId()).get().getPrecio()).isEqualTo(39.90);
    }

    @Test
    void actualizarPrecio() {

        Producto p = repo.findById(2L).orElseThrow();

        p.setPrecio(12.5);
        repo.save(p);

        assertThat(repo.findById(p.getId()).orElseThrow().getPrecio())
                .isEqualTo(12.5);
    }
}
