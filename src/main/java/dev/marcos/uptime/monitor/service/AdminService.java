package dev.marcos.uptime.monitor.service;
import dev.marcos.uptime.monitor.dto.response.MonitorSummaryResponse;
import dev.marcos.uptime.monitor.model.Monitor;
import dev.marcos.uptime.monitor.model.User;
import dev.marcos.uptime.monitor.repository.MonitorRepository;
import dev.marcos.uptime.monitor.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class AdminService {
    private final MonitorRepository monitorRepository;
    private final UserRepository userRepository;

    public AdminService(MonitorRepository monitorRepository, UserRepository userRepository) {
        this.monitorRepository = monitorRepository;
        this.userRepository = userRepository;
    }

    public List<MonitorSummaryResponse> getAllMonitorsOfUser(String username){
        User user;
        try {
             user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));
        }catch (UsernameNotFoundException e){
            log.warn("User {} not found", username);
            throw  e;
        }
        List<Monitor> monitors = monitorRepository.findMonitorByOwner(user);
        List<MonitorSummaryResponse> responses = new ArrayList<>();
        for (Monitor monitor : monitors) {
            responses.add(new MonitorSummaryResponse(
                    monitor.getId(),
                    monitor.getName(),
                    monitor.getUrl(),
                    monitor.getCurrentStatus(),
                    monitor.getActive()
            ));
        }
        return responses;
    }



}
