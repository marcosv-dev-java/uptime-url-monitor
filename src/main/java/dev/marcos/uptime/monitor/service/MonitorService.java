package dev.marcos.uptime.monitor.service;

import dev.marcos.uptime.monitor.dto.request.MonitorRequest;
import dev.marcos.uptime.monitor.dto.request.MonitorUpdateRequest;
import dev.marcos.uptime.monitor.dto.request.PauseRequest;
import dev.marcos.uptime.monitor.dto.response.InactiveMonitorResponse;
import dev.marcos.uptime.monitor.dto.response.MonitorResponse;
import dev.marcos.uptime.monitor.exceptions.MonitorNotFoundException;
import dev.marcos.uptime.monitor.model.Monitor;
import dev.marcos.uptime.monitor.model.User;
import dev.marcos.uptime.monitor.model.UserDetailsImpl;
import dev.marcos.uptime.monitor.repository.MonitorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import dev.marcos.uptime.monitor.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class MonitorService {
    private final MonitorRepository repository;
    private final UserRepository userRepository;

    public MonitorService(MonitorRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }
    private User getUserInContext(){
        var context = (UserDetailsImpl)Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        try {
            return userRepository.findByUsername(context.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Username " + context.getUsername() + " not found"));
        }catch (UsernameNotFoundException e){
            log.warn("Username {} not found", context.getUsername());
            throw e;
        }
    }
    private MonitorResponse entityToResponse(Monitor monitor){
        return new MonitorResponse(monitor.getId(), monitor.getName(), monitor.getUrl(), monitor.getLastCheckedAt(), monitor.getNextCheckDue(), monitor.getCurrentStatus(),
                monitor.getPausedUntil());
    }
    private Monitor getMonitorWithIDORPrevention(Long id){
        Monitor monitor;
        try {
            monitor = repository.findById(id)
                    .orElseThrow(() -> new MonitorNotFoundException("Monitor not found."));
        }catch (MonitorNotFoundException e){
            log.warn("Monitor {} not found.", id);
            throw e;
        }
        User userInContext = getUserInContext();
        if (!monitor.getOwner().getId().equals(userInContext.getId())) {
            log.warn("IDOR attempt: user {} tried to access monitor owned by {}", userInContext.getId(), monitor.getOwner().getId());
            throw new AccessDeniedException("User " + userInContext.getUsername() + " are not permitted for this request.");
        }
        return monitor;
    }

    public MonitorResponse createMonitor(MonitorRequest request)  {
        log.info("Creating monitor");
        Monitor monitor = new Monitor(request.name(),request.url(),request.intervalSeconds());
        User user = getUserInContext();
        monitor.setOwner(user);
        repository.save(monitor);
        return entityToResponse(monitor);
    }

    public MonitorResponse getMonitorById(Long id){
        log.info("Getting monitor with id {}", id);
        Monitor monitor =  getMonitorWithIDORPrevention(id);
        return entityToResponse(monitor);
    }
    public List<MonitorResponse> getAllMonitors(){
        log.info("Getting all monitors");
        return repository.findMonitorByOwner(getUserInContext())
                .stream()
                .map(this::entityToResponse)
                .toList();
    }

    public MonitorResponse updateMonitor(Long id, MonitorUpdateRequest request){
        log.info("Updating monitor with id {}", id);
        Monitor monitor = getMonitorWithIDORPrevention(id);
        if (!monitor.getActive()) throw new IllegalStateException("Cannot update a inactive monitor.");
        if (request.name() != null) monitor.setName(request.name());
        if (request.url() != null) monitor.setUrl(request.url());
        if (request.intervalSeconds() != null) monitor.setIntervalSeconds(request.intervalSeconds());
        repository.save(monitor);
        return entityToResponse(monitor);
    }

    public MonitorResponse softDeleteMonitor(Long id){
        log.info("Deleting monitor with id {}", id);
        Monitor monitor = getMonitorWithIDORPrevention(id);
        if (!monitor.getActive()) throw new IllegalStateException("Monitor already inactive.");
        monitor.setActive(false);
        repository.save(monitor);
        return entityToResponse(monitor);
    }

    public void pauseMonitorUntil(PauseRequest request, Long id){
        log.info("Pausing monitor with id {} for {} {} ", id, request.interval(), request.timeUnit().toString());
        Monitor monitor = getMonitorWithIDORPrevention(id);
        if (!monitor.getActive()) throw new IllegalStateException("Cannot pause a inactive monitor.");
        monitor.setPausedUntil(Instant.now().plus(request.interval(), request.timeUnit().toChronoUnit()));
        repository.save(monitor);
    }
    public void forceResumeMonitor(Long id){
        log.info("Resuming monitor with id {}", id);
        Monitor monitor = getMonitorWithIDORPrevention(id);
        if (!monitor.getActive()) throw new IllegalStateException("Cannot unpause a inactive monitor.");
        if (monitor.getPausedUntil() == null)
            throw new IllegalStateException("Monitor is not paused.");
        monitor.setPausedUntil(null);
        repository.save(monitor);
    }
    public List<InactiveMonitorResponse> getInactiveMonitors(){
        User user = getUserInContext();
        List<Monitor> monitors = repository.findInactiveMonitorByOwner(user);
        List<InactiveMonitorResponse> monitorResponses = new ArrayList<>();
        for (Monitor monitor : monitors) {
            monitorResponses.add(
                    new InactiveMonitorResponse(
                            monitor.getId(),
                            monitor.getName(),
                            monitor.getUrl(),
                            monitor.getLastCheckedAt(),
                            monitor.getCurrentStatus(),
                            monitor.getActive()
                    )
            );
        }
        return monitorResponses;
    }

}
