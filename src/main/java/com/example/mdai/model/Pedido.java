package com.example.mdai.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String numero;

    @Column(nullable = false)
    private double total;

    // 🔹 NUEVO: pedido pertenece a un usuario (cliente)
    @ManyToOne(optional = true) // de momento lo dejamos opcional para no romper formularios antiguos
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // 🔹 NUEVO: líneas de detalle del pedido
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    public Pedido() {}

    public Pedido(String numero, double total) {
        this.numero = numero;
        this.total = total;
    }

    // getters/setters
    public Long getId() { return id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    // 🔹 NUEVOS getters/setters
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }
    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }
}
