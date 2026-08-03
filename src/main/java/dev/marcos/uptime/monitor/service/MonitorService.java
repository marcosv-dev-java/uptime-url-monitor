package dev.marcos.uptime.monitor.service;

import dev.marcos.uptime.monitor.dto.request.MonitorRequest;
import dev.marcos.uptime.monitor.dto.response.MonitorResponse;
import dev.marcos.uptime.monitor.model.Monitor;
import dev.marcos.uptime.monitor.model.User;
import dev.marcos.uptime.monitor.model.UserDetailsImpl;
import dev.marcos.uptime.monitor.repository.MonitorRepository;
import dev.marcos.uptime.monitor.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MonitorService {
    private final MonitorRepository repository;
    private final UserRepository userRepository;

    public MonitorService(MonitorRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public MonitorResponse createMonitor(MonitorRequest request)  {
        Monitor monitor = new Monitor(request.name(),request.url(),request.intervalSeconds());
        var context = (UserDetailsImpl)Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        User user = userRepository.findByUsername(context.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username " + context.getUsername() + " not found"));
        monitor.setOwner(user);
        repository.save(monitor);
        return new MonitorResponse(monitor.getId(), monitor.getName(), monitor.getUrl(), monitor.getLastCheckedAt(), monitor.getNextCheckDue(), monitor.getCurrentStatus());

    }
}
