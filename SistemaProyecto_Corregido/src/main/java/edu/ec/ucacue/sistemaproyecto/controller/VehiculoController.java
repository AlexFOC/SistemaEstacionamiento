package edu.ec.ucacue.sistemaproyecto.controller;

import edu.ec.ucacue.sistemaproyecto.model.Vehiculo;
import edu.ec.ucacue.sistemaproyecto.service.VehiculoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    // Mostrar formulario de entrada
    @GetMapping("/formulario-entrada")
    public String mostrarFormularioEntrada(Model model) {
        model.addAttribute("vehiculo", new Vehiculo());
        return "entrada";
    }

    // Guardar entrada del vehículo
    @PostMapping("/registrar-entrada")
    public String registrarEntrada(@ModelAttribute Vehiculo vehiculo, Model model) {
        try {
            Vehiculo registrado = vehiculoService.registrarEntrada(vehiculo);
            model.addAttribute("vehiculo", registrado);
            return "entrada-confirmada";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "entrada";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "entrada";
        }
    }

    // Mostrar formulario de salida
    @GetMapping("/formulario-salida")
    public String mostrarFormularioSalida() {
        return "salida";
    }

    // Procesar salida del vehículo directamente
    @PostMapping("/registrar-salida")
    public String registrarSalidaDirecta(@RequestParam String placa, Model model) {
        try {
            double tarifa = 12.0;
            int tolerancia = 15;
            int redondeo = 30;

            // Registrar salida directamente
            Vehiculo vehiculo = vehiculoService.registrarSalida(placa, tarifa, tolerancia, redondeo);

            if (vehiculo == null) {
                model.addAttribute("error", "No se encontró un vehículo activo con la placa: " + placa);
                model.addAttribute("placa", placa);
                return "salida";
            }

            // La salida ya está registrada, mostrar confirmación
            model.addAttribute("vehiculo", vehiculo);
            model.addAttribute("mensaje", "Salida registrada exitosamente para el vehículo " + placa);
            return "salida-confirmada";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar la salida: " + e.getMessage());
            model.addAttribute("placa", placa);
            return "salida";
        }
    }
    
    @GetMapping("/historial")
    public String mostrarHistorial(Model model) {
        // Puedes cargar un historial inicial o dejarlo vacío para que el usuario busque
        model.addAttribute("historial", vehiculoService.obtenerTodosLosVehiculos());
        return "historial";
    }

    // Ver historial por placa
    @GetMapping("/historial-placa")
    public String verHistorial(@RequestParam String placa, Model model) {
        List<Vehiculo> historial = vehiculoService.obtenerHistorialPorPlaca(placa);
        model.addAttribute("historial", historial);
        return "historial";
    }

    // Validar formato de placa en tiempo real (AJAX)
    @PostMapping("/validar-placa")
    @ResponseBody
    public Map<String, Object> validarPlaca(@RequestParam String placa) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean valida = vehiculoService.validarFormatoPlaca(placa.toUpperCase());
            response.put("valida", valida);
            response.put("mensaje", valida ? "Formato válido" : "Formato inválido");
        } catch (Exception e) {
            response.put("valida", false);
            response.put("mensaje", "Error al validar: " + e.getMessage());
        }

        return response;
    }
}
