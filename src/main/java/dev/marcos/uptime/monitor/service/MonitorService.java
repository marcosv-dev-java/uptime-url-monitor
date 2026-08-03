package dev.marcos.uptime.monitor.service;

import dev.marcos.uptime.monitor.repository.MonitorRepository;
import org.springframework.stereotype.Service;

@Service
public class MonitorService {
    private MonitorRepository repository;

    public MonitorService(MonitorRepository repository) {
        this.repository = repository;
    }


    public MonitorResponse createMonitor(MonitorRequest request){

    }
}
