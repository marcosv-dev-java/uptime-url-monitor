package dev.marcos.uptime.monitor.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tb_check_result")
@Getter@Setter
@NoArgsConstructor
public class CheckResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false,name = "monitor_id")
    private Monitor monitor;
    @Column(name = "checked_at",nullable = false)
    private Instant checkedAt;
    @Column(name = "http_status")
    private Integer httpStatus;
    @Column(name = "response_time_ms",nullable = false)
    private Long responseTimeMs;
    @Column(nullable = false)
    private Boolean success;
    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    public CheckResult(Monitor monitor, Instant checkedAt, Integer httpStatus, Long responseTimeMs, Boolean success, String errorMessage) {
        this.monitor = monitor;
        this.checkedAt = checkedAt;
        this.httpStatus = httpStatus;
        this.responseTimeMs = responseTimeMs;
        this.success = success;
        this.errorMessage = errorMessage;
    }
}
