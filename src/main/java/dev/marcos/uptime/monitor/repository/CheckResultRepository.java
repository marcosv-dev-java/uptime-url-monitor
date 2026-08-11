package dev.marcos.uptime.monitor.repository;

import dev.marcos.uptime.monitor.model.CheckResult;
import dev.marcos.uptime.monitor.model.Monitor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface CheckResultRepository extends JpaRepository<CheckResult, Long> {

     Page<CheckResult>findByMonitor(Monitor monitor, Pageable pageable);

     @Query("SELECT (COUNT(CASE WHEN c.success = true THEN 1 END) * 100.0) / COUNT(c) " +
    "FROM CheckResult c " +
    "WHERE c.monitor = :monitor " +
    "AND c.checkedAt BETWEEN :from AND :to")
     Double findAverageBySuccess(@Param("from") Instant from,@Param("to") Instant to, @Param("monitor") Monitor monitor);
}
