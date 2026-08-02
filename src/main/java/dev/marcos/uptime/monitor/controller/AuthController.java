package dev.marcos.uptime.monitor.controller;

import dev.marcos.uptime.monitor.dto.request.LoginRequest;
import dev.marcos.uptime.monitor.dto.request.RegisterRequest;
import dev.marcos.uptime.monitor.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(service.loginVerification(request));
    }
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
    }
}
