package dev.marcos.uptime.monitor.scheduler;

import dev.marcos.uptime.monitor.model.Monitor;
import dev.marcos.uptime.monitor.repository.MonitorRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class MonitorScheduler {
    private final MonitorRepository monitorRepository;

    public MonitorScheduler(MonitorRepository monitorRepository) {
        this.monitorRepository = monitorRepository;
    }

    @Scheduled(fixedRate = 30000)
    public void checkMonitor(){
        System.out.println("Checking Monitor");
        List<Monitor> due = monitorRepository.findDueMonitors(Instant.now());
    }
    public void performCheck(Monitor monitor){
        Instant start = Instant.now();
        RestClient.create().get()
                .uri(monitor.getUrl())
                .retrieve()
                .toBodilessEntity();
        Instant end = Instant.now();
        long responseTimeMs = Duration.between(start, end).toMillis();

    }
}
