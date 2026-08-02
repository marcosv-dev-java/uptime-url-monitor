package dev.marcos.uptime.monitor.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tb_monitor")
public class Monitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;
    private String name;
    @Column(nullable = false)
    private String url;
    @Column(name = "interval_seconds")
    private Integer intervalSeconds;
    @Column(name = "last_checked_at")
    private Instant lastCheckedAt;
    @Column(name = "next_check_due")
    private Instant nextCheckDue;
    @Column(name = "current_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MonitorStatus currentStatus;
    @Column(nullable = false)
    private Boolean active;

    public Monitor(User owner, String name, String url) {
        this.owner = owner;
        this.name = name;
        this.url = url;
        this.active = true;
    }
}
