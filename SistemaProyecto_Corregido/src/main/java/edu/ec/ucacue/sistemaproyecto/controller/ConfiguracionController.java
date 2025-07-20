package edu.ec.ucacue.sistemaproyecto.controller;

import edu.ec.ucacue.sistemaproyecto.model.Configuracion;
import edu.ec.ucacue.sistemaproyecto.service.ConfiguracionService;
import edu.ec.ucacue.sistemaproyecto.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ConfiguracionController {

    @Autowired
    private ConfiguracionService configuracionService;

    @GetMapping("/configuracion")
    public String mostrarFormulario(Model model) {
        Configuracion config = configuracionService.obtenerConfiguracion();
        if (config == null) {
            config = new Configuracion(1.50, 15, 30); // Valores por defecto
            config.setValidacionPlacaActiva(true);
            config.setPatronPlaca("^[A-Z]{3}-\\d{3,4}$");
            config.setPaisSeleccionado("ECUADOR");
        }
        model.addAttribute("configuracion", config);
        return "configuracion";
    }

    @PostMapping("/configuracion")
    public String guardarConfiguracion(@ModelAttribute Configuracion configuracion, Model model) {
        configuracionService.guardarConfiguracion(configuracion);
        model.addAttribute("configuracion", configuracion);
        model.addAttribute("mensaje", "✅ Configuración guardada correctamente.");
        return "configuracion";
    }

    @PostMapping("/cambiar-pais")
    public String cambiarPatronPais(@RequestParam String pais, Model model) {
        Configuracion config = configuracionService.obtenerConfiguracion();
        if (config == null) {
            config = new Configuracion(1.50, 15, 30);
            config.setValidacionPlacaActiva(true);
        }
        config.setPaisSeleccionado(pais);
        config.setPatronPlaca(ConfiguracionService.obtenerPatronPorPais(pais));
        configuracionService.guardarConfiguracion(config);
        model.addAttribute("configuracion", config);
        model.addAttribute("mensaje", "✅ Patrón de placa actualizado para " + pais);
        return "configuracion";
    }
}
