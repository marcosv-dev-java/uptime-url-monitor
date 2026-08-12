package dev.marcos.uptime.monitor.dto.response;

import dev.marcos.uptime.monitor.model.MonitorStatus;

import java.time.Instant;

public record InactiveMonitorResponse(
        Long id,
        String name,
        String url,
        Instant lastCheckedAt,
        MonitorStatus lastMonitorStatus,
        Boolean isActive
) {
}
