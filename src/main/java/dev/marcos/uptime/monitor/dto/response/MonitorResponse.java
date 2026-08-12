package dev.marcos.uptime.monitor.dto.response;

import dev.marcos.uptime.monitor.model.MonitorStatus;

import java.time.Instant;

public record MonitorResponse(
        Long id,
        String name,
        String url,
        Instant lastCheckedAt,
        Instant nextCheckDue,
        MonitorStatus status,
        Instant pausedUntil
) {
}
