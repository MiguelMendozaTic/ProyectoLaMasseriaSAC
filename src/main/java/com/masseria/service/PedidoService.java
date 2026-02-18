package com.masseria.service;

import com.masseria.entity.Pedido;
import com.masseria.entity.Usuario;
import com.masseria.repository.PedidoRepository;
import com.masseria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.Objects;

@Service
@Transactional
public class PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // Constantes para los estados válidos
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_EN_PROCESO = "EN_PROCESO";
    private static final String ESTADO_ENVIADO = "ENVIADO";
    private static final String ESTADO_ENTREGADO = "ENTREGADO";
    private static final String ESTADO_CANCELADO = "CANCELADO";
    
    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }
    
    public Optional<Pedido> obtenerPorId(Long id) {
        return Optional.ofNullable(id)
                .flatMap(pedidoRepository::findById);
    }
    
    // Cambiado de clienteId a usuarioId
    public List<Pedido> obtenerPorUsuario(Long usuarioId) {
        return Optional.ofNullable(usuarioId)
                .map(pedidoRepository::findByUsuarioId)
                .orElse(Collections.emptyList());
    }
    
    public List<Pedido> obtenerPorEstado(String estado) {
        return Optional.ofNullable(estado)
                .filter(e -> !e.trim().isEmpty())
                .map(pedidoRepository::findByEstado)
                .orElse(Collections.emptyList());
    }
    
    public List<Pedido> obtenerPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            return Collections.emptyList();
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha inicio debe ser anterior a la fecha fin");
        }
        return pedidoRepository.findByFechaPedidoBetween(fechaInicio, fechaFin);
    }
    
    public Pedido guardar(Pedido pedido) {
        Objects.requireNonNull(pedido, "El pedido no puede ser nulo");
        
        Usuario usuario = pedido.getUsuario();
        if (usuario == null) {
            throw new IllegalArgumentException("El pedido debe tener un usuario asociado");
        }
        
        Long usuarioId = usuario.getId();
        if (usuarioId == null) {
            throw new IllegalArgumentException("El usuario no tiene un ID válido");
        }
        
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new IllegalArgumentException("El usuario especificado no existe");
        }
        
        if (!esEstadoValido(pedido.getEstado())) {
            pedido.setEstado(ESTADO_PENDIENTE);
        }
        
        return pedidoRepository.save(pedido);
    }
    
    // Método para guardar pedido solo con ID de usuario
    public Pedido guardarConUsuarioId(Pedido pedido, Long usuarioId) {
        Objects.requireNonNull(pedido, "El pedido no puede ser nulo");
        Objects.requireNonNull(usuarioId, "El ID del usuario no puede ser nulo");
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));
        
        pedido.setUsuario(usuario);
        
        if (!esEstadoValido(pedido.getEstado())) {
            pedido.setEstado(ESTADO_PENDIENTE);
        }
        
        return pedidoRepository.save(pedido);
    }
    
    public Pedido actualizarEstado(Long id, String nuevoEstado) {
        Objects.requireNonNull(id, "El id no puede ser nulo");
        Objects.requireNonNull(nuevoEstado, "El estado no puede ser nulo");
        
        if (nuevoEstado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede estar vacío");
        }
        
        if (!esEstadoValido(nuevoEstado)) {
            throw new IllegalArgumentException("Estado no válido: " + nuevoEstado + 
                ". Los estados permitidos son: " + obtenerEstadosPermitidos());
        }
        
        return pedidoRepository.findById(id)
            .map(pedido -> {
                pedido.setEstado(nuevoEstado);
                return pedidoRepository.save(pedido);
            })
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado con id: " + id));
    }
    
    public void eliminar(Long id) {
        Objects.requireNonNull(id, "El id no puede ser nulo");
        
        if (!pedidoRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: Pedido no encontrado con id: " + id);
        }
        pedidoRepository.deleteById(id);
    }
    
    private boolean esEstadoValido(String estado) {
        return estado != null && (
            estado.equals(ESTADO_PENDIENTE) ||
            estado.equals(ESTADO_EN_PROCESO) ||
            estado.equals(ESTADO_ENVIADO) ||
            estado.equals(ESTADO_ENTREGADO) ||
            estado.equals(ESTADO_CANCELADO)
        );
    }
    
    private String obtenerEstadosPermitidos() {
        return String.join(", ", 
            ESTADO_PENDIENTE, 
            ESTADO_EN_PROCESO, 
            ESTADO_ENVIADO, 
            ESTADO_ENTREGADO, 
            ESTADO_CANCELADO);
    }
    
    public List<Pedido> obtenerPedidosPendientes() {
        return pedidoRepository.findByEstado(ESTADO_PENDIENTE);
    }
    
    public List<Pedido> obtenerPedidosEnProceso() {
        return pedidoRepository.findByEstado(ESTADO_EN_PROCESO);
    }
    
    public List<Pedido> obtenerPedidosEntregados() {
        return pedidoRepository.findByEstado(ESTADO_ENTREGADO);
    }
    
    public List<Pedido> obtenerPedidosPorUsuarioRecientes(Long usuarioId) {
        return Optional.ofNullable(usuarioId)
                .map(pedidoRepository::findByUsuarioIdOrderByFechaPedidoDesc)
                .orElse(Collections.emptyList());
    }
    
    public boolean puedeCancelarPedido(Long id) {
        return obtenerPorId(id)
            .map(pedido -> 
                pedido.getEstado().equals(ESTADO_PENDIENTE) || 
                pedido.getEstado().equals(ESTADO_EN_PROCESO))
            .orElse(false);
    }
}