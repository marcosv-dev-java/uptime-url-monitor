package dev.marcos.uptime.monitor.service;

import dev.marcos.uptime.monitor.config.TokenService;
import dev.marcos.uptime.monitor.dto.request.LoginRequest;
import dev.marcos.uptime.monitor.dto.request.RegisterRequest;
import dev.marcos.uptime.monitor.dto.response.LoginResponse;
import dev.marcos.uptime.monitor.dto.response.RegisterResponse;
import dev.marcos.uptime.monitor.exceptions.UsernameAlreadyExistsException;
import dev.marcos.uptime.monitor.model.Role;
import dev.marcos.uptime.monitor.model.User;
import dev.marcos.uptime.monitor.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final TokenService tokenService;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final UserRepository repository;

    public AuthService(TokenService token, PasswordEncoder encoder, AuthenticationManager authManager, UserRepository repository) {
        this.tokenService = token;
        this.encoder = encoder;
        this.authManager = authManager;
        this.repository = repository;
    }

    public LoginResponse loginVerification(LoginRequest request){
        var usernamePassword = new UsernamePasswordAuthenticationToken(request.username(),request.password());
        var auth = authManager.authenticate(usernamePassword);
        User user = repository.findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Username " + auth.getName() + " not found."));
        String token = tokenService.generateToken(user);
        return new LoginResponse(token);
    }

    public RegisterResponse register(RegisterRequest request){
        if(repository.findByUsername(request.username()).isPresent()){
            throw new UsernameAlreadyExistsException("Username " + request.username() + " is already in use");
        }
        String encodedPassword = encoder.encode(request.password());
        User user = new User(request.username(), encodedPassword, Role.USER);
        repository.save(user);
        return new RegisterResponse(tokenService.generateToken(user));
    }

}
