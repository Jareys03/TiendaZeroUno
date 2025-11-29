package com.example.mdai.services;

import com.example.mdai.exception.ResourceNotFoundException;
import com.example.mdai.exception.ServiceException;
import com.example.mdai.model.Carrito;
import com.example.mdai.model.ItemCarrito;
import com.example.mdai.model.Pedido;
import com.example.mdai.repository.CarritoRepository;
import com.example.mdai.repository.ItemCarritoRepository;
import com.example.mdai.repository.PedidoRepository;
import com.example.mdai.model.DetallePedido;
import com.example.mdai.repository.DetallePedidoRepository;
import com.example.mdai.model.DetallePedido;
import com.example.mdai.model.Producto;
import com.example.mdai.model.Usuario;
import com.example.mdai.repository.ProductoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Iterator;


@Service
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                             CarritoRepository carritoRepository,
                             ItemCarritoRepository itemCarritoRepository,
                             DetallePedidoRepository detallePedidoRepository,
                             ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.carritoRepository = carritoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
    }


    // ---------- CRUD básico ----------

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findAll() {
        try {
            return pedidoRepository.findAll();
        } catch (Exception e) {
            throw new ServiceException("Error al listar pedidos", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> findById(Long id) {
        try {
            return pedidoRepository.findById(id);
        } catch (Exception e) {
            throw new ServiceException("Error al buscar pedido por id: " + id, e);
        }
    }

    @Override
    public Pedido save(Pedido pedido) {
        try {
            validarPedido(pedido, true);
            return pedidoRepository.save(pedido);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al guardar pedido", e);
        }
    }

    @Override
    public Pedido update(Long id, Pedido pedido) {
        try {
            validarPedido(pedido, false);

            return pedidoRepository.findById(id)
                    .map(existing -> {
                        // si cambia el número, comprobamos que no se repita
                        if (!existing.getNumero().equals(pedido.getNumero())) {
                            if (pedidoRepository.findByNumero(pedido.getNumero()).isPresent()) {
                                throw new ServiceException("Ya existe un pedido con ese número");
                            }
                        }
                        existing.setNumero(pedido.getNumero());
                        existing.setTotal(pedido.getTotal());
                        return pedidoRepository.save(existing);
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al actualizar pedido id: " + id, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            pedidoRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Error al eliminar pedido id: " + id, e);
        }
    }

    // ---------- Extras ----------

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findAllOrderByIdDesc() {
        try {
            return pedidoRepository.findAllByOrderByIdDesc();
        } catch (Exception e) {
            throw new ServiceException("Error al listar pedidos por id desc", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> findByNumero(String numero) {
        try {
            return pedidoRepository.findByNumero(numero);
        } catch (Exception e) {
            throw new ServiceException("Error al buscar pedido por número", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findByUsuario(Usuario usuario) {
        try {
            if (usuario == null || usuario.getId() == null) {
                throw new IllegalArgumentException("Usuario no válido para buscar pedidos");
            }
            return pedidoRepository.findByUsuario_IdOrderByIdDesc(usuario.getId());
        } catch (Exception e) {
            throw new ServiceException("Error al listar pedidos del usuario id: " + usuario.getId(), e);
        }
    }


    // ---------- CU-06: crear pedido desde carrito ----------

    @Override
    public Pedido crearDesdeCarrito(Long carritoId) {
        try {
            Carrito carrito = carritoRepository.findById(carritoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado con id: " + carritoId));

            List<ItemCarrito> items = itemCarritoRepository.findByCarrito_Id(carritoId);
            if (items.isEmpty()) {
                throw new IllegalArgumentException("No se puede procesar un carrito vacío");
            }

            double total = carrito.getTotal();
            if (total < 0) {
                throw new IllegalArgumentException("El total del carrito no puede ser negativo");
            }

            String numero = generarNumeroPedido();

// Crear pedido y asociar usuario
            Pedido pedido = new Pedido(numero, total);
            pedido.setUsuario(carrito.getUsuario()); // aquí enlazamos el pedido con el usuario

            validarPedido(pedido, true);

// Crear detalles a partir de los items del carrito
            for (ItemCarrito item : items) {
                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedido);
                detalle.setProducto(item.getProducto());
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(item.getPrecioUnitario());
                pedido.getDetalles().add(detalle);
            }

// Al tener cascade = ALL en Pedido.detalles, basta con guardar el pedido
            Pedido guardado = pedidoRepository.save(pedido);


            // "Marcar carrito como finalizado": aquí lo hacemos vaciando sus items.
            itemCarritoRepository.deleteByCarrito_Id(carritoId);
            carrito.getItems().clear();
            carritoRepository.save(carrito);

            return guardado;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al crear pedido desde carrito id: " + carritoId, e);
        }
    }

    @Override
    @Transactional
    public Pedido crearDesdeCarritoSesion(Map<Long, Integer> carrito, Usuario usuario) {

        if (carrito == null || carrito.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }

        // Número de pedido (usa tu lógica, o algo simple de momento)
        String numero = generarNumeroPedido(); // si no tienes este método, crea uno sencillo

        Pedido pedido = new Pedido();
        pedido.setNumero(numero);
        pedido.setUsuario(usuario);

        double total = 0.0;

        for (Map.Entry<Long, Integer> entry : carrito.entrySet()) {
            Long productoId = entry.getKey();
            Integer cantidad = entry.getValue();

            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Producto no encontrado con id " + productoId));

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecio());

            pedido.getDetalles().add(detalle);
            total += detalle.getSubtotal();
        }

        pedido.setTotal(total);

        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public Pedido actualizarPedidoDesdeCarritoSesion(Long pedidoId, Map<Long, Integer> carrito) {
        if (carrito == null || carrito.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido no encontrado con id: " + pedidoId));

        // Eliminar detalles antiguos (orphanRemoval = true en Pedido.detalles)
        pedido.getDetalles().clear();

        double total = 0.0;

        for (Map.Entry<Long, Integer> entry : carrito.entrySet()) {
            Long productoId = entry.getKey();
            Integer cantidad = entry.getValue();

            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Producto no encontrado con id " + productoId));

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecio());

            pedido.getDetalles().add(detalle);
            total += detalle.getSubtotal();
        }

        pedido.setTotal(total);
        return pedidoRepository.save(pedido);
    }



    // ---------- Helpers ----------

    private void validarPedido(Pedido pedido, boolean comprobarNumeroUnico) {
        if (pedido.getNumero() == null || pedido.getNumero().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de pedido no puede estar vacío");
        }
        if (pedido.getTotal() < 0) {
            throw new IllegalArgumentException("El total del pedido no puede ser negativo");
        }
        if (comprobarNumeroUnico &&
                pedidoRepository.findByNumero(pedido.getNumero()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un pedido con ese número");
        }
    }

    private String generarNumeroPedido() {
        long count = pedidoRepository.count() + 1;
        return "PED-" + count;
    }
}
