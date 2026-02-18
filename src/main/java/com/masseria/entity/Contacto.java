package com.masseria.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "contactos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String email;  // Cambiado de "correo" a "email"

    @Column(nullable = false, length = 255)
    private String asunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "leido")
    private Boolean leido = false;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    // RELACIÓN CON USUARIO (opcional, puede ser null si el contacto es anónimo)
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
    
    // Método helper para asociar automáticamente el email del usuario si existe
    public void setUsuarioAndEmail(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null && usuario.getEmail() != null) {
            this.email = usuario.getEmail();
        }
    }
}