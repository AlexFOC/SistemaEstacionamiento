package edu.ec.ucacue.sistemaproyecto.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; //libreria para encriptat contraseñas

public class EncriptarPassword {

    public static void main(String[] args) {
        // Crear una instancia de BCryptPasswordEncoder
        var password = "123"; // Contraseña a encriptar
        System.out.println("password" + password);
        System.out.println("Password encriptado: " + encriptarPassword(password));
    }

    public static String encriptarPassword(String password) {
        // Crear una instancia de BCryptPasswordEncoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return passwordEncoder.encode(password); // Encriptar la contraseña
    }

}
