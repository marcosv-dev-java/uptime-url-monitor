package dev.marcos.uptime.monitor.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record MonitorRequest(
        @NotEmpty String name,
        @NotEmpty String url,
        @NotNull Integer intervalSeconds
) {
}
