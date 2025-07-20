package edu.ec.ucacue.sistemaproyecto.repository;

import edu.ec.ucacue.sistemaproyecto.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // Métodos de búsqueda personalizados si son necesarios
}
