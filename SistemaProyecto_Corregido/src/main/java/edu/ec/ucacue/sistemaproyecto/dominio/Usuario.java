package edu.ec.ucacue.sistemaproyecto.dominio;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Entity
@Data
@Table(name = "usuario")
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L; // sirve para la serializacion de la clase que es
    // una interfaz que permite convertir un objeto en una secuencia de bytes
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    //anotacion que genera automaticamente el id,
    private long idUsuario;
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @OneToMany //establece una relacion uno a muchos con la tabla rol
    @JoinColumn(name = "id_usuario") //nombre de la columna que se va a relacionar
    private List<Rol> roles; //lista de roles que tiene el usuario
}
