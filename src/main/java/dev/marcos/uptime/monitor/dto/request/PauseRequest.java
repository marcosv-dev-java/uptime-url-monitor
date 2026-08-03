package dev.marcos.uptime.monitor.dto.request;

import dev.marcos.uptime.monitor.model.TimeUnit;
import jakarta.validation.constraints.NotNull;

public record PauseRequest(
        @NotNull Long id,
        @NotNull TimeUnit timeUnit,
        @NotNull Integer interval
        ) {
}
