package edu.ec.ucacue.sistemaproyecto.controller;

import edu.ec.ucacue.sistemaproyecto.model.Plaza;
import edu.ec.ucacue.sistemaproyecto.service.PlazaService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/plazas")
public class PlazaController {

    @Autowired
    private PlazaService plazaService;

    // Ruta específica para administración avanzada de plazas (vista)
    @GetMapping("/admin")
    public String mostrarPlazasAdmin(Model model) {
        List<Plaza> plazas = plazaService.listarTodas();
        plazas.sort(Comparator.comparing(Plaza::getId));

        // Agrupamos en bloques de 3x3 (9 plazas por bloque)
        int plazasPorBloque = 9;
        List<List<Plaza>> bloques = new ArrayList<>();
        for (int i = 0; i < plazas.size(); i += plazasPorBloque) {
            bloques.add(plazas.subList(i, Math.min(i + plazasPorBloque, plazas.size())));
        }

        // Calcular estadísticas
        long plazasLibres = plazas.stream().filter(p -> "libre".equals(p.getEstado())).count();
        long plazasOcupadas = plazas.stream().filter(p -> "ocupada".equals(p.getEstado())).count();
        long plazasReservadas = plazas.stream().filter(p -> "reservada".equals(p.getEstado())).count();
        long plazasMantenimiento = plazas.stream().filter(p -> "mantenimiento".equals(p.getEstado())).count();
        
        model.addAttribute("bloques", bloques);
        model.addAttribute("plazasLibres", plazasLibres);
        model.addAttribute("plazasOcupadas", plazasOcupadas);
        model.addAttribute("plazasReservadas", plazasReservadas);
        model.addAttribute("plazasMantenimiento", plazasMantenimiento);
        model.addAttribute("totalPlazas", plazas.size());
        model.addAttribute("porcentajeOcupacion", plazas.size() > 0 ? (plazasOcupadas * 100 / plazas.size()) : 0);
        
        return "plazas";
    }

    @PostMapping("/cambiar")
    public String cambiarEstado(@RequestParam String id) {
        plazaService.cambiarEstado(id);
        return "redirect:/plazas/admin"; // Actualizado para redirigir a la nueva ruta
    }

    // --- REST endpoints ---
    @RestController
    @RequestMapping("/plazas/api")
    public static class PlazaRestController {
        @Autowired
        private PlazaService plazaService;

        // Devuelve todas las plazas como JSON
        @GetMapping("/listar")
        public List<Plaza> listarPlazas() {
            return plazaService.listarTodas();
        }

        // Cambia el estado de la plaza a "mantenimiento"
        @PostMapping("/cambiar")
        public ResponseEntity<String> cambiarEstado(@RequestParam String id) {
            plazaService.cambiarEstado(id);
            return ResponseEntity.ok("Estado actualizado");
        }
    }
}
