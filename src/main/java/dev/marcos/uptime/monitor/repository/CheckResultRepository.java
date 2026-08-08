package dev.marcos.uptime.monitor.repository;

import dev.marcos.uptime.monitor.model.CheckResult;
import dev.marcos.uptime.monitor.model.Monitor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

public interface CheckResultRepository extends JpaRepository<CheckResult, Long> {

     Page<CheckResult>findByMonitor(Monitor monitor, Pageable pageable);
}
