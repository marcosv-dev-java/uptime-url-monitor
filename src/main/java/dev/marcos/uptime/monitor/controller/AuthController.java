package dev.marcos.uptime.monitor.controller;

import dev.marcos.uptime.monitor.config.TokenService;
import dev.marcos.uptime.monitor.dto.request.LoginRequest;
import dev.marcos.uptime.monitor.dto.response.LoginResponse;
import dev.marcos.uptime.monitor.model.User;
import dev.marcos.uptime.monitor.repository.UserDetailsServiceImpl;
import dev.marcos.uptime.monitor.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final TokenService tokenService;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;

    public AuthController(TokenService tokenService, PasswordEncoder encoder, AuthenticationManager authenticationManager, UserRepository repository) {
        this.tokenService = tokenService;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.repository = repository;
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody LoginRequest request){
        var usernamePassword = new UsernamePasswordAuthenticationToken(request.username(),request.password());
        var auth =  authenticationManager.authenticate(usernamePassword);
        User user = repository.findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Username " + auth.getName() + " not found."));
        String token = tokenService.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(token));
    }
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(){

    }
}
