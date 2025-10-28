package com.example.mdai.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrito")
public class Carrito {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrito> items = new ArrayList<>();

    protected Carrito() {}

    public Carrito(Usuario usuario) {
        this.usuario = usuario;
    }

    // helpers
    public void agregarProducto(Producto p, int cantidad) {
        ItemCarrito item = items.stream()
                .filter(i -> i.getProducto().getId().equals(p.getId()))
                .findFirst()
                .orElseGet(() -> {
                    ItemCarrito nuevo = new ItemCarrito(this, p, 0, p.getPrecio());
                    items.add(nuevo);
                    return nuevo;
                });
        item.setCantidad(item.getCantidad() + Math.max(1, cantidad));
    }

    public void eliminarProducto(Long productoId) {
        items.removeIf(i -> i.getProducto().getId().equals(productoId));
    }

    public double getTotal() {
        return items.stream().mapToDouble(ItemCarrito::getSubtotal).sum();
    }

    // getters/setters
    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public List<ItemCarrito> getItems() { return items; }
}

