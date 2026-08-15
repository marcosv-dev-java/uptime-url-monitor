package dev.marcos.uptime.monitor.scheduler;

import dev.marcos.uptime.monitor.model.CheckResult;
import dev.marcos.uptime.monitor.model.Monitor;
import dev.marcos.uptime.monitor.model.MonitorStatus;
import dev.marcos.uptime.monitor.repository.CheckResultRepository;
import dev.marcos.uptime.monitor.repository.MonitorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.List;



@Component
@Slf4j
public class MonitorScheduler {
    private final MonitorRepository monitorRepository;
    private final CheckResultRepository checkResultRepository;
    private final RestClient restClient;

    public MonitorScheduler(MonitorRepository monitorRepository, CheckResultRepository checkResultRepository, RestClient restClient) {
        this.monitorRepository = monitorRepository;
        this.checkResultRepository = checkResultRepository;
        this.restClient = restClient;
    }

    @Scheduled(fixedRate = 30000)
    public void checkMonitor(){
        log.debug("Checking monitor");
        List<Monitor> due = monitorRepository.findDueMonitors(Instant.now());
        due.forEach(this::performCheck);
    }
    public void performCheck(Monitor monitor){
        Instant start = Instant.now();
        ResponseEntity response;
        Integer httpCode = null;
        Long responseTimeMs;
        boolean success = false;
        String errorMessage = null;
        try {
            response = restClient.get()
                    .uri(monitor.getUrl())
                    .retrieve()
                    .toBodilessEntity();
             httpCode = response.getStatusCode().value();
             success = httpCode < 400;
            log.info("Check for monitor {} — status: {} httpCode: {} responseTime: {}ms",
                    monitor.getId(), success ? MonitorStatus.UP : MonitorStatus.DOWN , httpCode, Duration.between(start, Instant.now()).toMillis());
        }catch (Exception e){
            errorMessage = e.getMessage() != null ? e.getMessage().substring(0, Math.min(e.getMessage().length(), 500)) : null;
            log.warn("Check failed for monitor {} — {}", monitor.getId(), errorMessage);
        }
        finally {
            Instant end = Instant.now();
            responseTimeMs = Duration.between(start, end).toMillis();
            CheckResult result = new CheckResult(
                    monitor,
                    start,
                    httpCode,
                    responseTimeMs,
                    success,
                    errorMessage
            );
            checkResultRepository.save(result);
            monitor.setLastCheckedAt(result.getCheckedAt());
            Instant nextCheck = Instant.now().plus(Duration.ofSeconds(monitor.getIntervalSeconds()));
            monitor.setNextCheckDue(nextCheck);
            MonitorStatus status = success ? MonitorStatus.UP : MonitorStatus.DOWN;
            monitor.setCurrentStatus(status);
            monitorRepository.save(monitor);
        }
    }
}
