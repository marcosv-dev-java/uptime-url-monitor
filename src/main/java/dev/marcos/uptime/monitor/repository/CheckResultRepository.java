package dev.marcos.uptime.monitor.repository;

import dev.marcos.uptime.monitor.model.CheckResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckResultRepository extends JpaRepository<CheckResult, Long> {
}
