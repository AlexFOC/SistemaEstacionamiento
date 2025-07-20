package edu.ec.ucacue.sistemaproyecto.controller;

import edu.ec.ucacue.sistemaproyecto.model.Vehiculo;
import edu.ec.ucacue.sistemaproyecto.model.Reserva;
import edu.ec.ucacue.sistemaproyecto.service.VehiculoService;
import edu.ec.ucacue.sistemaproyecto.service.ReservaService;
import edu.ec.ucacue.sistemaproyecto.service.PlazaService;
import edu.ec.ucacue.sistemaproyecto.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {

    @Autowired
    private VehiculoService vehiculoService;
    
    @Autowired
    private ReservaService reservaService;
    
    @Autowired
    private PlazaService plazaService;
    
    @Autowired
    private VehiculoRepository vehiculoRepository;

    // === RUTAS PRINCIPALES ===
    
    @GetMapping("/entrada")
    public String mostrarFormularioEntrada(Model model) {
        model.addAttribute("vehiculo", new Vehiculo());
        return "entrada";
    }

    @GetMapping("/salida")
    public String mostrarFormularioSalida(Model model) {
        return "salida";
    }

    @GetMapping("/plazas")
    public String mostrarGestionPlazas(Model model) {
        // Agregar datos necesarios para la gestión de plazas
        model.addAttribute("totalPlazas", 50);
        model.addAttribute("plazasOcupadas", vehiculoService.obtenerVehiculosActivos().size());
        model.addAttribute("plazasLibres", 50 - vehiculoService.obtenerVehiculosActivos().size());
        return "plazas";
    }

    @GetMapping("/historial")
    public String mostrarHistorial(Model model) {
        // Mostrar historial general, con opción de búsqueda por placa
        List<Vehiculo> historialGeneral = vehiculoRepository.findAll().stream()
                .filter(v -> v.getHoraSalida() != null)
                .sorted((v1, v2) -> v2.getHoraSalida().compareTo(v1.getHoraSalida()))
                .limit(100) // Últimos 100 registros
                .collect(Collectors.toList());
        
        model.addAttribute("historial", historialGeneral);
        return "historial";
    }

    // === REPORTES PRINCIPALES ===
    
    @GetMapping("/reportes")
    public String mostrarReportes(Model model) {
        // Página principal de reportes con estadísticas generales
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioHoy = hoy.atStartOfDay();
        LocalDateTime finHoy = hoy.atTime(LocalTime.MAX);
        
        List<Vehiculo> vehiculosHoy = vehiculoRepository.findAll().stream()
                .filter(v -> v.getHoraSalida() != null &&
                           !v.getHoraSalida().isBefore(inicioHoy) &&
                           !v.getHoraSalida().isAfter(finHoy))
                .collect(Collectors.toList());
        
        double ingresosHoy = vehiculosHoy.stream()
                .mapToDouble(v -> v.getTotalPagar() != null ? v.getTotalPagar() : 0)
                .sum();
        
        model.addAttribute("vehiculosHoy", vehiculosHoy.size());
        model.addAttribute("ingresosHoy", ingresosHoy);
        model.addAttribute("fechaHoy", hoy);
        
        return "reportes";
    }

    // === NOTIFICACIONES ===
    
    @GetMapping("/notificaciones")
    public String mostrarNotificaciones(Model model) {
        return "notificaciones";
    }
}
