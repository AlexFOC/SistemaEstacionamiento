package edu.ec.ucacue.sistemaproyecto.service;

import edu.ec.ucacue.sistemaproyecto.dominio.Rol;
import edu.ec.ucacue.sistemaproyecto.dominio.Usuario;
import edu.ec.ucacue.sistemaproyecto.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("userDetailsService")
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UserDao usuarioDao;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioDao.findByUsername(username);

        if (usuario == null) {
            System.out.println("⚠️ Usuario no encontrado: " + username);
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        System.out.println("✅ Usuario encontrado: " + usuario.getUsername());
        System.out.println("🔑 Contraseña en DB: " + usuario.getPassword());

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Rol rol : usuario.getRoles()) {
            String nombreRol = rol.getNombre();
            // Transformar *_ROLE a ROLE_*
            if ("ADMIN_ROLE".equalsIgnoreCase(nombreRol)) {
                nombreRol = "ROLE_ADMIN";
            } else if ("USER_ROLE".equalsIgnoreCase(nombreRol)) {
                nombreRol = "ROLE_USER";
            } else if ("GUEST_ROLE".equalsIgnoreCase(nombreRol)) {
                nombreRol = "ROLE_GUEST";
            }
            System.out.println("Rol: " + nombreRol);
            authorities.add(new SimpleGrantedAuthority(nombreRol));
        }

        return new User(usuario.getUsername(), usuario.getPassword(), authorities);
    }
}