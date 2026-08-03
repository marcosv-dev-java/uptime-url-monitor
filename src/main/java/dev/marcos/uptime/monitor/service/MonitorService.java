package dev.marcos.uptime.monitor.service;

import dev.marcos.uptime.monitor.dto.request.MonitorRequest;
import dev.marcos.uptime.monitor.dto.response.MonitorResponse;
import dev.marcos.uptime.monitor.exceptions.MonitorNotFoundException;
import dev.marcos.uptime.monitor.model.Monitor;
import dev.marcos.uptime.monitor.model.User;
import dev.marcos.uptime.monitor.model.UserDetailsImpl;
import dev.marcos.uptime.monitor.repository.MonitorRepository;
import org.springframework.security.access.AccessDeniedException;
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
    private User getUserInContext(){
        var context = (UserDetailsImpl)Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        return userRepository.findByUsername(context.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username " + context.getUsername() + " not found"));
    }
    private MonitorResponse entityToResponse(Monitor monitor){
        return new MonitorResponse(monitor.getId(), monitor.getName(), monitor.getUrl(), monitor.getLastCheckedAt(), monitor.getNextCheckDue(), monitor.getCurrentStatus());
    }

    public MonitorResponse createMonitor(MonitorRequest request)  {
        Monitor monitor = new Monitor(request.name(),request.url(),request.intervalSeconds());
        User user = getUserInContext();
        monitor.setOwner(user);
        repository.save(monitor);
        return entityToResponse(monitor);
    }

    public MonitorResponse getMonitorById(Long id){
        Monitor monitor = repository.findById(id)
                .orElseThrow(() -> new MonitorNotFoundException("Monitor not found."));
        User userInContext = getUserInContext();
        if (!monitor.getOwner().equals(userInContext))
            throw new AccessDeniedException("User " + userInContext.getUsername() +  "Not permitted for this request.");
        return entityToResponse(monitor);
    }
}
