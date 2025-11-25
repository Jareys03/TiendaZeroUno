package com.example.mdai.services;

import com.example.mdai.model.Carrito;
import com.example.mdai.model.Producto;
import com.example.mdai.model.Usuario;
import com.example.mdai.repository.CarritoRepository;
import com.example.mdai.repository.ProductoRepository;
import com.example.mdai.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public CarritoServiceImpl(CarritoRepository carritoRepository,
                              UsuarioRepository usuarioRepository,
                              ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    // ---------- CRUD básico ----------

    @Override
    @Transactional(readOnly = true)
    public List<Carrito> findAll() {
        return carritoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Carrito> findById(Long id) {
        return carritoRepository.findById(id);
    }

    @Override
    public Carrito save(Carrito carrito) {
        return carritoRepository.save(carrito);
    }

    @Override
    public Carrito update(Long id, Carrito carrito) {
        return carritoRepository.findById(id)
                .map(existing -> {
                    existing.setUsuario(carrito.getUsuario());
                    existing.setItems(carrito.getItems());
                    return carritoRepository.save(existing);
                })
                .orElseThrow(() ->
                        new IllegalArgumentException("Carrito no encontrado con id: " + id));
    }

    @Override
    public void deleteById(Long id) {
        carritoRepository.deleteById(id);
    }

    // ---------- LÓGICA DEL CARRO SEGÚN CASOS DE USO ----------

    @Override
    public Carrito obtenerOCrearCarritoPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));

        // Usamos TU método del repositorio:
        return carritoRepository.findByUsuario_Id(usuarioId)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito(usuario);
                    return carritoRepository.save(nuevo);
                });
    }

    @Override
    public Carrito agregarProducto(Long usuarioId, Long productoId, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
        }

        Carrito carrito = obtenerOCrearCarritoPorUsuario(usuarioId);

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Producto no encontrado con ID: " + productoId));

        // Usa tu helper: sumar cantidades, no duplicar, guardar precio_unitario correcto
        carrito.agregarProducto(producto, cantidad);

        return carritoRepository.save(carrito);
    }

    @Override
    public Carrito eliminarProducto(Long usuarioId, Long productoId) {
        Carrito carrito = obtenerOCrearCarritoPorUsuario(usuarioId);

        carrito.eliminarProducto(productoId);

        return carritoRepository.save(carrito);
    }
}
