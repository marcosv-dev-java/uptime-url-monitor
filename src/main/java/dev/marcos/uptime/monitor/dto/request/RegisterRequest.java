package dev.marcos.uptime.monitor.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record RegisterRequest(
        @NotEmpty String username,
        @NotEmpty String password
) {
}
