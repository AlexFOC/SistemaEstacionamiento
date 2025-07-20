package edu.ec.ucacue.sistemaproyecto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ec.ucacue.sistemaproyecto.model.Vehiculo;
import edu.ec.ucacue.sistemaproyecto.repository.VehiculoRepository;
import edu.ec.ucacue.sistemaproyecto.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    @Autowired
    private VehiculoRepository vehiculoRepository;
    @Autowired
    private ReservaService reservaService;

@GetMapping("/dashboard")
public String mostrarDashboard(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
        Model model) {

    // Si no se pasan fechas, usar el día actual
    LocalDate hoy = LocalDate.now();
    if (desde == null) desde = hoy;
    if (hasta == null) hasta = hoy;

    LocalDateTime inicio = desde.atStartOfDay();
    LocalDateTime fin = hasta.atTime(LocalTime.MAX);

    List<Vehiculo> todos = vehiculoRepository.findAll();

    List<Vehiculo> vehiculosActivos = todos.stream()
            .filter(v -> v.getHoraSalida() == null)
            .toList();

    List<Vehiculo> vehiculosFiltrados = todos.stream()
            .filter(v -> v.getHoraSalida() != null &&
                         !v.getHoraSalida().isBefore(inicio) &&
                         !v.getHoraSalida().isAfter(fin))
            .toList();

    long ocupadas = vehiculosActivos.size();
    long libres = 50 - ocupadas;

    double ingresos = vehiculosFiltrados.stream()
            .mapToDouble(v -> v.getTotalPagar() != null ? v.getTotalPagar() : 0)
            .sum();

    // Ingresos por hora
    Map<Integer, Double> ingresosPorHora = new TreeMap<>();
    for (int i = 0; i < 24; i++) ingresosPorHora.put(i, 0.0);

    for (Vehiculo v : vehiculosFiltrados) {
        if (v.getHoraSalida() != null && v.getTotalPagar() != null) {
            int hora = v.getHoraSalida().getHour();
            ingresosPorHora.put(hora, ingresosPorHora.get(hora) + v.getTotalPagar());
        }
    }

    model.addAttribute("ocupadas", ocupadas);
    model.addAttribute("libres", libres);
    model.addAttribute("ingresos", ingresos);
    model.addAttribute("reservasActivas", reservaService.obtenerReservasActivas().size());

    // Para que Thymeleaf lo inyecte como objeto JS y no como string, pasamos el mapa directamente
    model.addAttribute("ingresosPorHoraJson", ingresosPorHora);

    model.addAttribute("desde", desde);
    model.addAttribute("hasta", hasta);

    return "dashboard";
}
}


