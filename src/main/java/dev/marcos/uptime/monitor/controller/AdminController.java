package dev.marcos.uptime.monitor.controller;

import dev.marcos.uptime.monitor.dto.response.MonitorSummaryResponse;
import dev.marcos.uptime.monitor.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@EnableMethodSecurity
@RequestMapping("/admin")
public class AdminController {
    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    @GetMapping("/user/{username}/monitors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MonitorSummaryResponse>> getAllMonitorsOfUser(@PathVariable String username){
        return ResponseEntity.ok(service.getAllMonitorsOfUser(username));
    }
}
