package com.masseria.service;

import com.masseria.entity.Reservacion;
import com.masseria.repository.ReservacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservacionService {
    @Autowired
    private ReservacionRepository reservacionRepository;

    public List<Reservacion> obtenerTodas() {
        return reservacionRepository.findAll();
    }

    public List<Reservacion> obtenerPorFecha(LocalDate fecha) {
        return reservacionRepository.findByFecha(fecha);
    }

    public List<Reservacion> obtenerPorEstado(String estado) {
        return reservacionRepository.findByEstado(estado);
    }

    public Optional<Reservacion> obtenerPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return reservacionRepository.findById(id);
    }

    public Reservacion guardar(Reservacion reservacion) {
        if (reservacion == null) {
            return null;
        }
        return reservacionRepository.save(reservacion);
    }

    public void eliminar(Long id) {
        if (id != null) {
            reservacionRepository.deleteById(id);
        }
    }

    public void confirmar(Long id) {
        if (id != null) {
            Optional<Reservacion> reservacion = reservacionRepository.findById(id);
            reservacion.ifPresent(r -> {
                r.setEstado("Confirmada");
                reservacionRepository.save(r);
            });
        }
    }

    public void cancelar(Long id) {
        if (id != null) {
            Optional<Reservacion> reservacion = reservacionRepository.findById(id);
            reservacion.ifPresent(r -> {
                r.setEstado("Cancelada");
                reservacionRepository.save(r);
            });
        }
    }
}
