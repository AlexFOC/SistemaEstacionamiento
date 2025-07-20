package edu.ec.ucacue.sistemaproyecto.dao;
import edu.ec.ucacue.sistemaproyecto.dominio.Persona;
import org.springframework.data.repository.CrudRepository;

public interface PersonaDao extends CrudRepository<Persona, Long>{ //
    //CrudRepository es una interfaz que nos permite realizar operaciones CRUD (Crear, Leer, Actualizar y Eliminar) en la base de datos
    //el primer parametro es la clase que queremos manipular, en este caso persona
    //el segundo parametro es el tipo de dato del id, en este caso long
//estamos heredando de la clase crud repositorio, y que vea el id de la persona

}
