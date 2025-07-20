package edu.ec.ucacue.sistemaproyecto.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.validation.constraints.Pattern(
        regexp = "^[A-Z]{3}-\\d{3,4}$",
        message = "Formato de placa inválido. Debe ser ABC-123 o ABC-1234."
    )
    @Column(nullable = false, unique = false)
    private String placa;

    @Column(nullable = false)
    private String tipo; // Ej: "carro", "moto"

    private String plazaAsignada;

    private LocalDateTime horaEntrada;

    private LocalDateTime horaSalida;

    private Double totalPagar;

    private String metodoPago; // Método de pago utilizado

    private boolean reserva; // Por si se implementan reservas

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPlazaAsignada() {
        return plazaAsignada;
    }

    public void setPlazaAsignada(String plazaAsignada) {
        this.plazaAsignada = plazaAsignada;
    }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalDateTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalDateTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalDateTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public Double getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(Double totalPagar) {
        this.totalPagar = totalPagar;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public boolean isReserva() {
        return reserva;
    }

    public void setReserva(boolean reserva) {
        this.reserva = reserva;
    }
}
