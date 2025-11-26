package com.example.mdai.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pedido")
public class Pedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String numero;

    @Column(nullable = false)
    private double total;

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


}
