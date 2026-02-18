package com.masseria.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String email;  // Cambiado de "correo" a "email"

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(name = "cantidad_personas", nullable = false)
    private Integer cantidadPersonas;

    @Column(length = 500)
    private String notas;

    @Column(name = "estado", length = 50)
    @Builder.Default
    private String estado = "PENDIENTE"; // PENDIENTE, CONFIRMADA, CANCELADA

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Relación ManyToOne con Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
    
    // Constructor personalizado para crear reservaciones desde formulario
    public Reservacion(String nombre, String email, String telefono, 
                      LocalDate fecha, LocalTime hora, Integer cantidadPersonas, 
                      String notas, Usuario usuario) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.fecha = fecha;
        this.hora = hora;
        this.cantidadPersonas = cantidadPersonas;
        this.notas = notas;
        this.usuario = usuario;
        this.estado = "PENDIENTE";
        this.fechaCreacion = LocalDateTime.now();
    }
    
    // Método helper para asociar usuario automáticamente
    public void setUsuarioAndEmail(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null && usuario.getEmail() != null) {
            this.email = usuario.getEmail();
        }
    }
    
    // Métodos de negocio
    public void confirmar() {
        this.estado = "CONFIRMADA";
    }
    
    public void cancelar() {
        this.estado = "CANCELADA";
    }
    
    public boolean estaPendiente() {
        return "PENDIENTE".equals(this.estado);
    }
    
    public boolean estaConfirmada() {
        return "CONFIRMADA".equals(this.estado);
    }
    
    public boolean estaCancelada() {
        return "CANCELADA".equals(this.estado);
    }
    
    // Método de fábrica para crear reservaciones
    public static Reservacion crearReservacion(String nombre, String email, String telefono,
                                              LocalDate fecha, LocalTime hora, 
                                              Integer cantidadPersonas, Usuario usuario) {
        return Reservacion.builder()
            .nombre(nombre)
            .email(email)
            .telefono(telefono)
            .fecha(fecha)
            .hora(hora)
            .cantidadPersonas(cantidadPersonas)
            .usuario(usuario)
            .estado("PENDIENTE")
            .fechaCreacion(LocalDateTime.now())
            .build();
    }
}