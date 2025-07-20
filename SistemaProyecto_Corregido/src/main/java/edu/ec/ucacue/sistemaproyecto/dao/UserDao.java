package edu.ec.ucacue.sistemaproyecto.dao;

import edu.ec.ucacue.sistemaproyecto.dominio.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDao extends JpaRepository<Usuario, Long> { //jparepository es una interfaz
    // que nos permite realizar operaciones CRUD (Crear, Leer, Actualizar y Eliminar) en la base de datos

    @Query("SELECT u FROM Usuario u JOIN FETCH u.roles WHERE u.username = :username")
    Usuario findByUsername(@Param("username") String username);

}
