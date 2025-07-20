package edu.ec.ucacue.sistemaproyecto.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "plaza")
public class Plaza {

    @Id
    private String id; // Por ejemplo: A1, B2, etc.

    private String estado; // libre, ocupada, reservada, mantenimiento

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
