package edu.ec.ucacue.sistemaproyecto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
.authorizeHttpRequests(auth -> auth
    // Acceso público
    .requestMatchers(
        "/login",
        "/guest-login",
        "/css/**",
        "/js/**",
        "/images/**"
    ).permitAll()
    // Permitir acceso público al endpoint de login de invitados
    .requestMatchers("/guest-login").permitAll()
    // Lectura para todos
    .requestMatchers(
        "/dashboard",
        "/vehiculos/historial",
        "/plazas/admin"
    ).hasAnyRole("USER", "ADMIN", "GUEST")
    // Registros y validación solo para USER y ADMIN
    .requestMatchers(
        "/vehiculos/validar-placa",
        "/vehiculos/registrar-entrada",
        "/vehiculos/registrar-salida"
    ).hasAnyRole("USER", "ADMIN")
    // Resto de funciones
    .requestMatchers("/reservas/**", "/configuracion").hasAnyRole("USER", "ADMIN")
    .requestMatchers("/configuracion", "/reportes", "/plazas/gestionar").hasRole("ADMIN")
    .anyRequest().authenticated()
)
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/dashboard", true)
            .failureUrl("/login?error=true")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
        )
        .build();
}

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
