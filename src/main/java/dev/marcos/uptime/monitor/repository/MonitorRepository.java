package dev.marcos.uptime.monitor.repository;

import dev.marcos.uptime.monitor.model.Monitor;
import dev.marcos.uptime.monitor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MonitorRepository extends JpaRepository<Monitor, Long> {

    Optional<Monitor> findByName(String name);
    Optional<Monitor> findByUrl(String url);
    @Query("SELECT m FROM Monitor m WHERE m.owner = :user")
    List<Monitor> findMonitorByOwner(@Param("user")User user);

}
