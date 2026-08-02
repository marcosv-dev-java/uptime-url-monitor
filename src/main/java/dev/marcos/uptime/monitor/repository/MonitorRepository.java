package dev.marcos.uptime.monitor.repository;

import dev.marcos.uptime.monitor.model.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonitorRepository extends JpaRepository<Monitor, Long> {

    Optional<Monitor> findByName(String name);
    Optional<Monitor> findByUrl(String url);
}
