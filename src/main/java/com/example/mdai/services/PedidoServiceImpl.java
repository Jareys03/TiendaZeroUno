package com.example.mdai.services;

import com.example.mdai.exception.ResourceNotFoundException;
import com.example.mdai.exception.ServiceException;
import com.example.mdai.model.Carrito;
import com.example.mdai.model.ItemCarrito;
import com.example.mdai.model.Pedido;
import com.example.mdai.repository.CarritoRepository;
import com.example.mdai.repository.ItemCarritoRepository;
import com.example.mdai.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                             CarritoRepository carritoRepository,
                             ItemCarritoRepository itemCarritoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.carritoRepository = carritoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
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

            Pedido pedido = new Pedido(numero, total);
            validarPedido(pedido, true);

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
