package dev.marcos.uptime.monitor.controller;

import dev.marcos.uptime.monitor.dto.request.MonitorRequest;
import dev.marcos.uptime.monitor.dto.request.MonitorUpdateRequest;
import dev.marcos.uptime.monitor.dto.request.PauseRequest;
import dev.marcos.uptime.monitor.dto.response.CheckResultSummaryResponse;
import dev.marcos.uptime.monitor.dto.response.InactiveMonitorResponse;
import dev.marcos.uptime.monitor.dto.response.MonitorResponse;
import dev.marcos.uptime.monitor.dto.response.UptimePercentageResponse;
import dev.marcos.uptime.monitor.service.CheckResultService;
import dev.marcos.uptime.monitor.service.MonitorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/monitors")
public class MonitorController {
    private final MonitorService service;
    private final CheckResultService checkService;

    public MonitorController(MonitorService service, CheckResultService checkService) {
        this.service = service;
        this.checkService = checkService;
    }

    @GetMapping
    public ResponseEntity<List<MonitorResponse>> getAllMonitors() {
        return ResponseEntity.ok(service.getAllMonitors());
    }
    @GetMapping("/{id}")
    public ResponseEntity<MonitorResponse> getMonitorById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMonitorById(id));
    }
    @PostMapping
    public ResponseEntity<MonitorResponse> createMonitor(@RequestBody @Valid MonitorRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createMonitor(request));
    }
    @PutMapping("/{id}")
    public ResponseEntity<MonitorResponse> updateMonitor(@PathVariable Long id, @RequestBody  @Valid MonitorUpdateRequest request){
        return ResponseEntity.ok().body(service.updateMonitor(id,request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<MonitorResponse> softDeleteMonitor(@PathVariable Long id){
        return ResponseEntity.ok().body(service.softDeleteMonitor(id));
    }
    @PutMapping("/{id}/pause")
    public ResponseEntity<Void> pauseMonitor(@PathVariable Long id,@RequestBody @Valid PauseRequest request){
        service.pauseMonitorUntil(request, id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}/unpause")
    public ResponseEntity<Void> resumeMonitor(@PathVariable Long id){
        service.forceResumeMonitor(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/checks")
    public ResponseEntity<List<CheckResultSummaryResponse>> getPageOfChecksMonitor(@PathVariable Long id,
                                                                                   @RequestParam(defaultValue = "0") Integer pageNumber){
        return ResponseEntity.ok().body(checkService.getMonitorCheckHistory(id, pageNumber));
    }
    @GetMapping("/{id}/checks/uptime-percentage")
    public ResponseEntity<UptimePercentageResponse> getUptimePercentageMonitor(@PathVariable Long id,
                                                                               @RequestParam Instant from,
                                                                               @RequestParam Instant to){
        return ResponseEntity.ok().body(checkService.getPercentOfSuccessInPeriod(from,to,id));
    }
    @GetMapping("/inactive")
    public ResponseEntity<List<InactiveMonitorResponse>> getInactiveMonitors(){
        return ResponseEntity.ok(service.getInactiveMonitors());
    }


}
