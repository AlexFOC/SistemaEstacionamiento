package edu.ec.ucacue.sistemaproyecto.service;

import edu.ec.ucacue.sistemaproyecto.model.Plaza;
import edu.ec.ucacue.sistemaproyecto.repository.PlazaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlazaService {

    @Autowired
    private PlazaRepository plazaRepository;

    // Lista todas las plazas
    public List<Plaza> listarTodas() {
        return plazaRepository.findAll();
    }

    // Devuelve la primera plaza libre disponible
    public Plaza obtenerPrimeraLibre() {
        return plazaRepository.findAll().stream()
                .filter(p -> "libre".equals(p.getEstado()))
                .findFirst()
                .orElse(null);
    }

    // Marca una plaza como ocupada
    public void ocuparPlaza(String id) {
        Plaza plaza = plazaRepository.findById(id).orElse(null);
        if (plaza != null) {
            plaza.setEstado("ocupada");
            plazaRepository.save(plaza);
        }
    }

    // Libera una plaza ocupada
    public void liberarPlaza(String id) {
        Plaza plaza = plazaRepository.findById(id).orElse(null);
        if (plaza != null) {
            plaza.setEstado("libre");
            plazaRepository.save(plaza);
        }
    }

    // Cambia manualmente el estado (usado en la vista visual)
    public void cambiarEstado(String id) {
        Plaza plaza = plazaRepository.findById(id).orElse(null);
        if (plaza != null) {
            if ("mantenimiento".equals(plaza.getEstado())) {
                plaza.setEstado("libre"); // Si ya está en mantenimiento, la libera
            } else {
                plaza.setEstado("mantenimiento"); // Si no, la pone en mantenimiento
            }
            plazaRepository.save(plaza);
        }
    }
    
    public Plaza obtenerPlazaPorId(String id) {
        return plazaRepository.findById(id).orElse(null);
    }
}



