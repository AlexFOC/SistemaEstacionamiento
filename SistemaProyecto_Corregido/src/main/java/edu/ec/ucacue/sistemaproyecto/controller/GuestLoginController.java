package edu.ec.ucacue.sistemaproyecto.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class GuestLoginController {

    @GetMapping("/guest-login")
    public String guestLogin() {
        // Auto-login como GUEST
        Authentication auth = new UsernamePasswordAuthenticationToken(
            "guest", null,
            List.of(new SimpleGrantedAuthority("ROLE_GUEST"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        return "redirect:/dashboard";
    }
}