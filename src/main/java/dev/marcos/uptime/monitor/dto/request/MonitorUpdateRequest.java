package dev.marcos.uptime.monitor.dto.request;

public record MonitorUpdateRequest(
        String name,
        String url,
        Integer intervalSeconds
) {
}
