package dev.marcos.uptime.monitor.model;

import java.time.temporal.ChronoUnit;

public enum TimeUnit {
    MINUTE(ChronoUnit.MINUTES),
    HOUR(ChronoUnit.HOURS),
    DAY(ChronoUnit.DAYS);

    private final ChronoUnit chronoUnit;

    TimeUnit(ChronoUnit chronoUnit) {
        this.chronoUnit = chronoUnit;
    }

    public ChronoUnit toChronoUnit() {
        return chronoUnit;
    }
}
