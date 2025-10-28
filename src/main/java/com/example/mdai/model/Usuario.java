package com.example.mdai.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=80)
    private String nombre;

    @Column(nullable=false, unique=true, length=150)
    private String correo;

    // Relación 1..N con Direccion
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Direccion> direcciones = new ArrayList<>();

    protected Usuario() { }

    public Usuario(String nombre, String correo) {
        this.nombre = nombre;
        this.correo = correo;
    }

    // ===== Helpers para mantener ambos lados sincronizados =====
    public void agregarDireccion(Direccion d) {
        if (d == null) return;
        direcciones.add(d);
        d.setUsuario(this);
    }

    public void quitarDireccion(Direccion d) {
        if (d == null) return;
        direcciones.remove(d);
        d.setUsuario(null);
    }

    // ===== Getters/Setters =====
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public List<Direccion> getDirecciones() { return direcciones; }
    public void setDirecciones(List<Direccion> direcciones) { this.direcciones = direcciones; }
}
