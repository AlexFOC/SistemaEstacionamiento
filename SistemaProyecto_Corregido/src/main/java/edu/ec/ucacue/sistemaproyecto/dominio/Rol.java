package edu.ec.ucacue.sistemaproyecto.dominio;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;

@Entity
@Data
@Table(name = "rol")
public class Rol implements Serializable {
    private static final long serialVersionUID = 1L; // sirve para la serializacion de la clase que es
    // una interfaz que permite convertir un objeto en una secuencia de bytes
    @Id //anotacion para indicar que es la llave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) //anotacion que genera automaticamente el id,
    // en este caso es autoincremental que
    // queire decir que cada vez que se inserte un nuevo registro, el id se incrementa automaticamente
    private long idRol;

    @NotEmpty //anotacion para indicar que no puede ser nulo
    private String nombre;
}
