package edu.ec.ucacue.sistemaproyecto.service;

import edu.ec.ucacue.sistemaproyecto.model.Configuracion;
import edu.ec.ucacue.sistemaproyecto.model.Plaza;
import edu.ec.ucacue.sistemaproyecto.model.Vehiculo;
import edu.ec.ucacue.sistemaproyecto.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private PlazaService plazaService;

    @Autowired
    private ConfiguracionService configuracionService;

    /**
     * Registrar entrada de un vehículo asignando una plaza libre automáticamente
     */
    public Vehiculo registrarEntrada(Vehiculo vehiculo) {
        // Validación fija antes de la validación por configuración
        if (!validarFormatoPlaca(vehiculo.getPlaca())) {
            throw new RuntimeException("Formato de placa inválido. Verifique el formato configurado.");
        }

        // Asignación manual de la plaza desde el formulario
        if (vehiculo.getPlazaAsignada() == null || vehiculo.getPlazaAsignada().isEmpty()) {
            throw new IllegalArgumentException("Debe asignar un número de plaza.");
        }

        Plaza plaza = plazaService.obtenerPlazaPorId(vehiculo.getPlazaAsignada());
        if (plaza == null) {
            throw new IllegalArgumentException("La plaza " + vehiculo.getPlazaAsignada() + " no existe.");
        }

        if (!"libre".equals(plaza.getEstado())) {
            throw new IllegalArgumentException("La plaza " + vehiculo.getPlazaAsignada() + " no está disponible. Estado actual: " + plaza.getEstado().toUpperCase());
        }

        plazaService.ocuparPlaza(vehiculo.getPlazaAsignada());

        vehiculo.setHoraEntrada(LocalDateTime.now());
        vehiculo.setHoraSalida(null);
        vehiculo.setTotalPagar(null);

        return vehiculoRepository.save(vehiculo);
    }


    /**
     * Registrar salida, calcular tiempo y total a pagar, liberar plaza
     */
    public Vehiculo registrarSalida(String placa, double tarifaPorHora, int toleranciaMinutos, int redondeoMinutos) {
        Optional<Vehiculo> vehiculoOpt = vehiculoRepository
                .findTopByPlacaAndHoraSalidaIsNullOrderByHoraEntradaDesc(placa);

        if (vehiculoOpt.isPresent()) {
            Vehiculo vehiculo = vehiculoOpt.get();
            LocalDateTime salida = LocalDateTime.now();

            vehiculo.setHoraSalida(salida);

            long minutos = Duration.between(vehiculo.getHoraEntrada(), salida).toMinutes();
            double totalPagar = calcularCosto(minutos, tarifaPorHora, toleranciaMinutos, redondeoMinutos);
            vehiculo.setTotalPagar(totalPagar);

            plazaService.liberarPlaza(vehiculo.getPlazaAsignada());

            return vehiculoRepository.save(vehiculo);
        }

        return null;
    }

    /**
     * Cálculo del costo con redondeo por bloques y tolerancia
     */
    private double calcularCosto(long minutos, double tarifaPorHora, int tolerancia, int redondeoMin) {
        if (minutos <= 0) {
            return tarifaPorHora;
        }

        if (minutos <= 60 + tolerancia) {
            return tarifaPorHora;
        }

        long minutosExtra = minutos - 60;
        double bloques = Math.ceil(minutosExtra / (double) redondeoMin);

        // Cada bloque = media tarifa
        return tarifaPorHora + (bloques * (tarifaPorHora / 2));
    }

    /**
     * Ver historial de entradas/salidas por placa
     */
    public List<Vehiculo> obtenerHistorialPorPlaca(String placa) {
        return vehiculoRepository.findByPlacaOrderByHoraEntradaDesc(placa);
    }

    /**
     * Verifica si una plaza está ocupada
     */
    public boolean plazaOcupada(String plaza) {
        return vehiculoRepository.existsByPlazaAsignadaAndHoraSalidaIsNull(plaza);
    }

    /**
     * Validación del formato de placa según configuración activa
     */
    public boolean validarFormatoPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            return false;
        }

        Configuracion config = configuracionService.obtenerConfiguracion();
        if (config == null || !config.isValidacionPlacaActiva()) {
            return true;
        }

        String patron = config.getPatronPlaca();
        if (patron == null || patron.trim().isEmpty()) {
            return true;
        }

        return placa.trim().toUpperCase().matches(patron);
    }

    /**
     * Patrones predefinidos por país (para selección en la configuración)
     */
    public static String obtenerPatronPorPais(String pais) {
        switch (pais.toUpperCase()) {
            case "ECUADOR":
                return "^[A-Z]{3}-[0-9]{3,4}$"; // ABC-123 o ABC-1234
            case "COLOMBIA":
                return "^[A-Z]{3}[0-9]{3}$";     // ABC123
            case "PERU":
                return "^[A-Z]{3}-[0-9]{3}$";   // ABC-123
            case "MEXICO":
                return "^[A-Z]{3}-[0-9]{2}-[0-9]{2}$"; // ABC-12-34
            case "PERSONALIZADO":
                return "^[A-Z0-9\\-]{5,10}$";   // Flexible
            default:
                return "^[A-Z]{3}-[0-9]{3,4}$"; // Ecuador por defecto
        }
    }

    /**
     * Buscar vehículo activo por placa (sin hora de salida)
     */
    public Vehiculo buscarVehiculoActivoPorPlaca(String placa) {
        Optional<Vehiculo> vehiculoOpt = vehiculoRepository
                .findTopByPlacaAndHoraSalidaIsNullOrderByHoraEntradaDesc(placa);
        return vehiculoOpt.orElse(null);
    }

    /**
     * Calcular costo de estacionamiento sin registrar la salida
     */
    public Map<String, Object> calcularCostoEstacionamiento(Vehiculo vehiculo, double tarifaPorHora, int toleranciaMinutos, int redondeoMinutos) {
        Map<String, Object> resultado = new HashMap<>();
        
        LocalDateTime ahora = LocalDateTime.now();
        long minutos = Duration.between(vehiculo.getHoraEntrada(), ahora).toMinutes();
        
        // Formatear tiempo estacionado
        long horas = minutos / 60;
        long minutosRestantes = minutos % 60;
        String tiempoEstacionado = horas + "h " + minutosRestantes + "m";
        
        // Calcular costo
        double totalPagar = calcularCosto(minutos, tarifaPorHora, toleranciaMinutos, redondeoMinutos);
        
        // Tiempo base para mostrar
        String tiempoBase = tiempoEstacionado;
        if (minutos <= 60 + toleranciaMinutos) {
            tiempoBase = "1h (incluye " + toleranciaMinutos + " min de tolerancia)";
        }
        
        resultado.put("tiempoEstacionado", tiempoEstacionado);
        resultado.put("tiempoBase", tiempoBase);
        resultado.put("totalPagar", totalPagar);
        resultado.put("minutosTotal", minutos);
        
        return resultado;
    }

    /**
     * Obtener lista de vehículos actualmente estacionados (sin hora de salida)
     */
    public List<Vehiculo> obtenerVehiculosActivos() {
        return vehiculoRepository.findAll().stream()
                .filter(v -> v.getHoraSalida() == null)
                .sorted((v1, v2) -> v1.getHoraEntrada().compareTo(v2.getHoraEntrada()))
                .toList();
    }

    /**
     * Obtener todos los vehículos (activos e históricos)
     */
    public List<Vehiculo> obtenerTodosLosVehiculos() {
        return vehiculoRepository.findAll().stream()
                .sorted((v1, v2) -> {
                    LocalDateTime fecha1 = v1.getHoraSalida() != null ? v1.getHoraSalida() : v1.getHoraEntrada();
                    LocalDateTime fecha2 = v2.getHoraSalida() != null ? v2.getHoraSalida() : v2.getHoraEntrada();
                    return fecha2.compareTo(fecha1); // Más recientes primero
                })
                .toList();
    }
}
