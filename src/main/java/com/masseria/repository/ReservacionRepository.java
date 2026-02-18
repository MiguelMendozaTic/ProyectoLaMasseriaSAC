package com.masseria.repository;

import com.masseria.entity.Reservacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservacionRepository extends JpaRepository<Reservacion, Long> {
    List<Reservacion> findByFecha(LocalDate fecha);
    List<Reservacion> findByEstado(String estado);
}
