package com.example.mdai.services;

import com.example.mdai.exception.ResourceNotFoundException;
import com.example.mdai.exception.ServiceException;
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
        try {
            return carritoRepository.findAll();
        } catch (Exception e) {
            throw new ServiceException("Error al listar carritos", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Carrito> findById(Long id) {
        try {
            return carritoRepository.findById(id);
        } catch (Exception e) {
            throw new ServiceException("Error al buscar carrito por id: " + id, e);
        }
    }

    @Override
    public Carrito save(Carrito carrito) {
        try {
            return carritoRepository.save(carrito);
        } catch (Exception e) {
            throw new ServiceException("Error al guardar carrito", e);
        }
    }

    @Override
    public Carrito update(Long id, Carrito carrito) {
        try {
            return carritoRepository.findById(id)
                    .map(existing -> {
                        existing.setUsuario(carrito.getUsuario());
                        existing.setItems(carrito.getItems());
                        return carritoRepository.save(existing);
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado con id: " + id));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al actualizar carrito id: " + id, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            carritoRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Error al eliminar carrito id: " + id, e);
        }
    }

    // ---------- LÓGICA DEL CARRO SEGÚN CASOS DE USO ----------

    @Override
    public Carrito obtenerOCrearCarritoPorUsuario(Long usuarioId) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

            return carritoRepository.findByUsuario_Id(usuarioId)
                    .orElseGet(() -> {
                        Carrito nuevo = new Carrito(usuario);
                        return carritoRepository.save(nuevo);
                    });
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al obtener o crear carrito para usuario id: " + usuarioId, e);
        }
    }

    @Override
    public Carrito agregarProducto(Long usuarioId, Long productoId, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
        }

        try {
            Carrito carrito = obtenerOCrearCarritoPorUsuario(usuarioId);

            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));

            carrito.agregarProducto(producto, cantidad);

            return carritoRepository.save(carrito);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al agregar producto al carrito usuarioId: " + usuarioId + " productoId: " + productoId, e);
        }
    }

    @Override
    public Carrito eliminarProducto(Long usuarioId, Long productoId) {
        try {
            Carrito carrito = obtenerOCrearCarritoPorUsuario(usuarioId);

            carrito.eliminarProducto(productoId);

            return carritoRepository.save(carrito);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al eliminar producto del carrito usuarioId: " + usuarioId + " productoId: " + productoId, e);
        }
    }
}
