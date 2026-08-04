package dev.marcos.uptime.monitor.dto.response;

public record ErrorResponse(
        int status,
        String message
) {
}
