package edu.ec.ucacue.sistemaproyecto.model;

import jakarta.persistence.*;

@Entity
public class Configuracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === Configuración de Tarifas ===
    @Column
    private double tarifaPorHora; // Ej: 12.00

    @Column
    private int toleranciaMinutos; // Ej: 15

    @Column
    private int redondeoMinutos; // Ej: 30

    // === Validación de Placas ===
    @Column
    private boolean validacionPlacaActiva = true;

    @Column
    private String patronPlaca = "^[A-Z]{3}-[0-9]{3,4}$"; // Ecuador: ABC-123 o ABC-1234

    @Column
    private String paisSeleccionado = "ECUADOR";

    // === Constructores ===
    public Configuracion() {
    }

    public Configuracion(double tarifaPorHora, int toleranciaMinutos, int redondeoMinutos) {
        this.tarifaPorHora = tarifaPorHora;
        this.toleranciaMinutos = toleranciaMinutos;
        this.redondeoMinutos = redondeoMinutos;
    }

    // === Getters y Setters ===
    public Long getId() {
        return id;
    }

    public double getTarifaPorHora() {
        return tarifaPorHora;
    }

    public void setTarifaPorHora(double tarifaPorHora) {
        this.tarifaPorHora = tarifaPorHora;
    }

    public int getToleranciaMinutos() {
        return toleranciaMinutos;
    }

    public void setToleranciaMinutos(int toleranciaMinutos) {
        this.toleranciaMinutos = toleranciaMinutos;
    }

    public int getRedondeoMinutos() {
        return redondeoMinutos;
    }

    public void setRedondeoMinutos(int redondeoMinutos) {
        this.redondeoMinutos = redondeoMinutos;
    }

    public boolean isValidacionPlacaActiva() {
        return validacionPlacaActiva;
    }

    public void setValidacionPlacaActiva(boolean validacionPlacaActiva) {
        this.validacionPlacaActiva = validacionPlacaActiva;
    }

    public String getPatronPlaca() {
        return patronPlaca;
    }

    public void setPatronPlaca(String patronPlaca) {
        this.patronPlaca = patronPlaca;
    }

    public String getPaisSeleccionado() {
        return paisSeleccionado;
    }

    public void setPaisSeleccionado(String paisSeleccionado) {
        this.paisSeleccionado = paisSeleccionado;
    }
}
