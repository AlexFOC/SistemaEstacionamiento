package edu.ec.ucacue.sistemaproyecto.service;

import edu.ec.ucacue.sistemaproyecto.model.Configuracion;
import edu.ec.ucacue.sistemaproyecto.repository.ConfiguracionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfiguracionService {

    @Autowired
    private ConfiguracionRepository configuracionRepository;

    public Configuracion obtenerConfiguracion() {
        java.util.List<Configuracion> todas = configuracionRepository.findAll();
        if (todas.isEmpty()) {
            return null;
        }
        return todas.get(0);
    }

    public void guardarConfiguracion(Configuracion configuracion) {
        configuracionRepository.save(configuracion);
    }

    public static String obtenerPatronPorPais(String pais) {
        switch (pais.toUpperCase()) {
            case "ECUADOR":
                return "^[A-Z]{3}-\\d{3,4}$"; // ABC-123 o ABC-1234
            case "COLOMBIA":
                return "^[A-Z]{3}\\d{3}$"; // ABC123
            case "PERU":
                return "^[A-Z]{2}\\d{3}[A-Z]{2}$"; // AB123CD
            default:
                return "^[A-Z]{3}-\\d{3,4}$"; // Por defecto Ecuador
        }
    }
}
