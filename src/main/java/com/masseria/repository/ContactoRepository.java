package com.masseria.repository;

import com.masseria.entity.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Long> {
    
    List<Contacto> findByLeidoFalse();
    
    List<Contacto> findByUsuarioId(Long usuarioId);
    
    List<Contacto> findByEmail(String email);
    
    // Opción 1: Usar la convención de nomenclatura correcta (RECOMENDADO)
    List<Contacto> findAllByOrderByFechaCreacionDesc();
    
    // Opción 2: Usar @Query explícitamente
    @Query("SELECT c FROM Contacto c ORDER BY c.fechaCreacion DESC")
    List<Contacto> obtenerTodosOrdenados();
    
    // Opción 3: Si quieres los no leídos ordenados
    List<Contacto> findByLeidoFalseOrderByFechaCreacionDesc();
}