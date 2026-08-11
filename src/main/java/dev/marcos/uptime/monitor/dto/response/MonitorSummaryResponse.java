package dev.marcos.uptime.monitor.dto.response;

import dev.marcos.uptime.monitor.model.MonitorStatus;

public record MonitorSummaryResponse(
        Long id,
        String name,
        String url,
        MonitorStatus status,
        Boolean active
) {
}
