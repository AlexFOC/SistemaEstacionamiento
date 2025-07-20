package edu.ec.ucacue.sistemaproyecto.controller;

import edu.ec.ucacue.sistemaproyecto.model.Reserva;
import edu.ec.ucacue.sistemaproyecto.service.ReservaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/gestion-reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // Mostrar página principal de gestión de reservas
    @GetMapping
    public String mostrarGestionReservas(Model model) {
        try {
            List<Reserva> reservasActivas = reservaService.obtenerReservasActivas();
            List<Reserva> todasLasReservas = reservaService.obtenerTodasLasReservas();
            
            // Calcular estadísticas
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalReservas", todasLasReservas.size());
            stats.put("reservasActivas", reservasActivas.size());
            
            // Reservas de hoy
            LocalDate hoy = LocalDate.now();
            long reservasHoy = todasLasReservas.stream()
                .filter(r -> r.getFechaInicio().toLocalDate().equals(hoy))
                .count();
            stats.put("reservasHoy", reservasHoy);
            
            // Reservas vencidas (activas pero con fecha de fin pasada)
            LocalDateTime ahora = LocalDateTime.now();
            long reservasVencidas = reservasActivas.stream()
                .filter(r -> r.getFechaFin().isBefore(ahora))
                .count();
            stats.put("reservasVencidas", reservasVencidas);
            
            model.addAttribute("reservas", reservasActivas);
            model.addAttribute("stats", stats);
            model.addAttribute("reserva", new Reserva()); // Para el formulario de nueva reserva
            return "gestion-reservas-simple";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las reservas: " + e.getMessage());
            model.addAttribute("reservas", new ArrayList<>());
            
            // Estadísticas por defecto en caso de error
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalReservas", 0);
            stats.put("reservasActivas", 0);
            stats.put("reservasHoy", 0);
            stats.put("reservasVencidas", 0);
            model.addAttribute("stats", stats);
            
            return "gestion-reservas-simple";
        }
    }

    // Mostrar formulario para crear nueva reserva
    @GetMapping("/nueva")
    public String mostrarFormularioNuevaReserva(Model model) {
        model.addAttribute("reserva", new Reserva());
        return "nueva-reserva";
    }

    @PostMapping("/crear")
    public String crearReserva(@RequestParam Long vehiculoId, @RequestParam String plazaId,
                              @RequestParam String fechaInicioStr, @RequestParam String fechaFinStr, Model model) {
        try {
            LocalDateTime fechaInicio = LocalDateTime.parse(fechaInicioStr);
            LocalDateTime fechaFin = LocalDateTime.parse(fechaFinStr);
            Reserva reserva = reservaService.crearReserva(vehiculoId, plazaId, fechaInicio, fechaFin);
            model.addAttribute("mensaje", "Reserva creada exitosamente para la plaza " + reserva.getPlaza().getId() + ".");
            return "redirect:/gestion-reservas"; // Redirigir a la lista de reservas
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "nueva-reserva"; // Volver a la vista de creación con mensaje de error
        }
    }

    @PostMapping("/cancelar")
    public String cancelarReserva(@RequestParam Long reservaId, Model model) {
        try {
            reservaService.cancelarReserva(reservaId);
            model.addAttribute("mensaje", "Reserva cancelada correctamente.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/gestion-reservas"; // Redirigir a la lista de reservas
    }

    // Ver detalles de una reserva específica
    @GetMapping("/{id}")
    public String verDetalleReserva(@PathVariable Long id, Model model) {
        try {
            Reserva reserva = reservaService.obtenerReservaPorId(id);
            if (reserva != null) {
                model.addAttribute("reserva", reserva);
                return "detalle-reserva";
            } else {
                model.addAttribute("error", "Reserva no encontrada.");
                return "redirect:/gestion-reservas";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar la reserva: " + e.getMessage());
            return "redirect:/gestion-reservas";
        }
    }
}

