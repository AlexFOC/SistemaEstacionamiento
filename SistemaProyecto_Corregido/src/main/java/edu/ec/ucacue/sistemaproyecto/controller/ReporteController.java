package edu.ec.ucacue.sistemaproyecto.controller;

import edu.ec.ucacue.sistemaproyecto.model.Vehiculo;
import edu.ec.ucacue.sistemaproyecto.repository.VehiculoRepository;
import edu.ec.ucacue.sistemaproyecto.service.VehiculoService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReporteController {

    @Autowired
    private VehiculoRepository vehiculoRepository;
    
    @Autowired
    private VehiculoService vehiculoService;

    /**
     * Reporte Diario - Muestra datos del día actual
     */
    @GetMapping("/reportes/diario")
    public String reporteDiario(Model model) {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.atTime(LocalTime.MAX);
        
        Map<String, Object> datosReporte = generarDatosReporte(inicio, fin);
        
        model.addAttribute("tipoReporte", "Diario");
        model.addAttribute("fechaInicio", hoy.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("fechaFin", hoy.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAllAttributes(datosReporte);
        
        return "reportes";
    }
    
    /**
     * Reporte Semanal - Muestra datos de la semana actual
     */
    @GetMapping("/reportes/semanal")
    public String reporteSemanal(Model model) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.minusDays(hoy.getDayOfWeek().getValue() - 1); // Lunes
        LocalDate finSemana = inicioSemana.plusDays(6); // Domingo
        
        LocalDateTime inicio = inicioSemana.atStartOfDay();
        LocalDateTime fin = finSemana.atTime(LocalTime.MAX);
        
        Map<String, Object> datosReporte = generarDatosReporte(inicio, fin);
        
        model.addAttribute("tipoReporte", "Semanal");
        model.addAttribute("fechaInicio", inicioSemana.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("fechaFin", finSemana.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAllAttributes(datosReporte);
        
        return "reportes";
    }
    
    /**
     * Reporte Mensual - Muestra datos del mes actual
     */
    @GetMapping("/reportes/mensual")
    public String reporteMensual(Model model) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());
        
        LocalDateTime inicio = inicioMes.atStartOfDay();
        LocalDateTime fin = finMes.atTime(LocalTime.MAX);
        
        Map<String, Object> datosReporte = generarDatosReporte(inicio, fin);
        
        model.addAttribute("tipoReporte", "Mensual");
        model.addAttribute("fechaInicio", inicioMes.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("fechaFin", finMes.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAllAttributes(datosReporte);
        
        return "reportes";
    }
    
    /**
     * Método auxiliar para generar los datos del reporte
     */
    private Map<String, Object> generarDatosReporte(LocalDateTime inicio, LocalDateTime fin) {
        Map<String, Object> datos = new HashMap<>();
        
        // Obtener vehículos que salieron en el período
        List<Vehiculo> vehiculosSalida = vehiculoRepository.findAll().stream()
            .filter(v -> v.getHoraSalida() != null &&
                         !v.getHoraSalida().isBefore(inicio) &&
                         !v.getHoraSalida().isAfter(fin))
            .toList();
        
        // Obtener vehículos que entraron en el período
        List<Vehiculo> vehiculosEntrada = vehiculoRepository.findAll().stream()
            .filter(v -> v.getHoraEntrada() != null &&
                         !v.getHoraEntrada().isBefore(inicio) &&
                         !v.getHoraEntrada().isAfter(fin))
            .toList();
        
        // Calcular estadísticas
        int totalEntradas = vehiculosEntrada.size();
        int totalSalidas = vehiculosSalida.size();
        
        double montoTotal = vehiculosSalida.stream()
            .mapToDouble(v -> v.getTotalPagar() != null ? v.getTotalPagar() : 0.0)
            .sum();
        
        // Calcular tiempo total de estacionamiento
        long tiempoTotalMinutos = vehiculosSalida.stream()
            .filter(v -> v.getHoraEntrada() != null && v.getHoraSalida() != null)
            .mapToLong(v -> Duration.between(v.getHoraEntrada(), v.getHoraSalida()).toMinutes())
            .sum();
        
        String tiempoTotalFormateado = formatearTiempo(tiempoTotalMinutos);
        
        datos.put("totalEntradas", totalEntradas);
        datos.put("totalSalidas", totalSalidas);
        datos.put("montoTotal", String.format("%.2f", montoTotal));
        datos.put("tiempoTotal", tiempoTotalFormateado);
        datos.put("vehiculosDetalle", vehiculosSalida);
        
        return datos;
    }
    
    /**
     * Formatear tiempo en minutos a formato legible
     */
    private String formatearTiempo(long minutos) {
        long horas = minutos / 60;
        long minutosRestantes = minutos % 60;
        long dias = horas / 24;
        long horasRestantes = horas % 24;
        
        if (dias > 0) {
            return String.format("%d días, %d horas, %d minutos", dias, horasRestantes, minutosRestantes);
        } else if (horas > 0) {
            return String.format("%d horas, %d minutos", horas, minutosRestantes);
        } else {
            return String.format("%d minutos", minutos);
        }
    }

@GetMapping("/reporte/txt")
public void generarReporteTxt(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
        HttpServletResponse response) throws Exception {

    LocalDateTime inicio = desde.atStartOfDay();
    LocalDateTime fin = hasta.atTime(LocalTime.MAX);

    List<Vehiculo> lista = vehiculoRepository.findAll().stream()
        .filter(v -> v.getHoraSalida() != null &&
                     !v.getHoraSalida().isBefore(inicio) &&
                     !v.getHoraSalida().isAfter(fin))
        .toList();

    response.setContentType("text/plain");
    response.setHeader("Content-Disposition", "attachment; filename=reporte_" + desde + "_a_" + hasta + ".txt");

    PrintWriter writer = response.getWriter();
    writer.println("Reporte de Ingresos");
    writer.println("Desde: " + desde + "  Hasta: " + hasta + "\n");

    double total = 0;
    for (Vehiculo v : lista) {
        double monto = v.getTotalPagar() != null ? v.getTotalPagar() : 0;
        writer.printf("Placa: %s | Tipo: %s | Entrada: %s | Salida: %s | Total: $%.2f%n",
                v.getPlaca(), v.getTipo(), v.getHoraEntrada(), v.getHoraSalida(), monto);
        total += monto;
    }

    writer.println("\nTotal Recaudado: $" + String.format("%.2f", total));
    writer.flush();
}


@GetMapping("/reporte/excel")
public void generarReporteExcel(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
        HttpServletResponse response) throws Exception {

    LocalDateTime inicio = desde.atStartOfDay();
    LocalDateTime fin = hasta.atTime(LocalTime.MAX);

    List<Vehiculo> lista = vehiculoRepository.findAll().stream()
        .filter(v -> v.getHoraSalida() != null &&
                     !v.getHoraSalida().isBefore(inicio) &&
                     !v.getHoraSalida().isAfter(fin))
        .toList();

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Reporte");

    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("Placa");
    header.createCell(1).setCellValue("Tipo");
    header.createCell(2).setCellValue("Hora Entrada");
    header.createCell(3).setCellValue("Hora Salida");
    header.createCell(4).setCellValue("Total Pagado");

    int rowNum = 1;
    double total = 0;
    for (Vehiculo v : lista) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(v.getPlaca());
        row.createCell(1).setCellValue(v.getTipo());
        row.createCell(2).setCellValue(String.valueOf(v.getHoraEntrada()));
        row.createCell(3).setCellValue(String.valueOf(v.getHoraSalida()));
        double pago = v.getTotalPagar() != null ? v.getTotalPagar() : 0;
        row.createCell(4).setCellValue(pago);
        total += pago;
    }

    Row totalRow = sheet.createRow(rowNum);
    totalRow.createCell(3).setCellValue("Total");
    totalRow.createCell(4).setCellValue(total);

    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("Content-Disposition", "attachment; filename=reporte_" + desde + "_a_" + hasta + ".xlsx");

    workbook.write(response.getOutputStream());
    workbook.close();
}

}
