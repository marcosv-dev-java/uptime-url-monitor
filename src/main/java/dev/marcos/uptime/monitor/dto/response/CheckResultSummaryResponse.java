package dev.marcos.uptime.monitor.dto.response;

import java.time.Instant;

public record CheckResultSummaryResponse(
        Instant checkedAt,
        Integer httpStatus,
        Long responseTimeMs,
        Boolean success,
        String errorMessage
) {
}
