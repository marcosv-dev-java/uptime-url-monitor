package dev.marcos.uptime.monitor.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UptimePercentageRequest(
        @NotNull Instant startDate,
        @NotNull Instant endDate,
        @NotNull Long monitorId
) {
}
