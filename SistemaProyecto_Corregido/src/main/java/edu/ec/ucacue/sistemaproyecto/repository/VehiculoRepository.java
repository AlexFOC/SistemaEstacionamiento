package edu.ec.ucacue.sistemaproyecto.repository;

import edu.ec.ucacue.sistemaproyecto.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    // Buscar vehículos por placa (útil para salida o historial)
    Optional<Vehiculo> findTopByPlacaAndHoraSalidaIsNullOrderByHoraEntradaDesc(String placa);

    // Buscar todos los registros por placa
    List<Vehiculo> findByPlacaOrderByHoraEntradaDesc(String placa);

    // Verificar si una plaza está ocupada actualmente
    boolean existsByPlazaAsignadaAndHoraSalidaIsNull(String plazaAsignada);
}
