package edu.ec.ucacue.sistemaproyecto.service;

import edu.ec.ucacue.sistemaproyecto.model.Plaza;
import edu.ec.ucacue.sistemaproyecto.model.Reserva;
import edu.ec.ucacue.sistemaproyecto.model.Vehiculo;
import edu.ec.ucacue.sistemaproyecto.repository.PlazaRepository;
import edu.ec.ucacue.sistemaproyecto.repository.ReservaRepository;
import edu.ec.ucacue.sistemaproyecto.repository.VehiculoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final PlazaRepository plazaRepository;
    private final VehiculoRepository vehiculoRepository;

    public ReservaService(ReservaRepository reservaRepository, PlazaRepository plazaRepository, VehiculoRepository vehiculoRepository) {
        this.reservaRepository = reservaRepository;
        this.plazaRepository = plazaRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    public Reserva crearReserva(Long vehiculoId, String plazaId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));
        Plaza plaza = plazaRepository.findById(plazaId)
                .orElseThrow(() -> new IllegalArgumentException("Plaza no encontrada"));

        // Verificar disponibilidad de la plaza para el período de reserva y que no esté en mantenimiento
        if (!"libre".equals(plaza.getEstado()) && !"reservada".equals(plaza.getEstado())) {
            throw new IllegalStateException("La plaza no está disponible para reservas (estado actual: " + plaza.getEstado() + ").");
        }

        if (!isPlazaDisponible(plaza, fechaInicio, fechaFin)) {
            throw new IllegalStateException("La plaza ya está reservada para el período seleccionado.");
        }

        Reserva reserva = new Reserva();
        reserva.setVehiculo(vehiculo);
        reserva.setPlaza(plaza);
        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFin(fechaFin);
        reserva.setEstado("ACTIVA");
        plaza.setEstado("reservada");
        plazaRepository.save(plaza);

        return reservaRepository.save(reserva);
    }

    private boolean isPlazaDisponible(Plaza plaza, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        // Lógica para verificar si la plaza está ocupada por otra reserva activa en el mismo período
        List<Reserva> reservasConflictivas = reservaRepository.findAll().stream()
                .filter(r -> r.getPlaza().getId().equals(plaza.getId()) && r.getEstado().equals("ACTIVA") &&
                        (fechaInicio.isBefore(r.getFechaFin()) && fechaFin.isAfter(r.getFechaInicio())))
                .toList();

        return reservasConflictivas.isEmpty();
    }

    public void cancelarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        reserva.setEstado("CANCELADA");
        Plaza plaza = reserva.getPlaza();
        if (plaza != null) {
            plaza.setEstado("libre");
            plazaRepository.save(plaza);
        }
        reservaRepository.save(reserva);
    }

    public List<Reserva> obtenerReservasActivas() {
        return reservaRepository.findAll().stream()
                .filter(r -> "ACTIVA".equals(r.getEstado()))
                .sorted((r1, r2) -> r2.getFechaInicio().compareTo(r1.getFechaInicio()))
                .toList();
    }

    public List<Reserva> obtenerTodasLasReservas() {
        return reservaRepository.findAll().stream()
                .sorted((r1, r2) -> r2.getFechaInicio().compareTo(r1.getFechaInicio()))
                .toList();
    }

    public List<Reserva> obtenerReservasPorEstado(String estado) {
        return reservaRepository.findAll().stream()
                .filter(r -> estado.equals(r.getEstado()))
                .sorted((r1, r2) -> r2.getFechaInicio().compareTo(r1.getFechaInicio()))
                .toList();
    }

    public Reserva obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id).orElse(null);
    }
}
