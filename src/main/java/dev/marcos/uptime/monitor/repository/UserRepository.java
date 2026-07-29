package dev.marcos.uptime.monitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dev.marcos.uptime.monitor.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

     Optional<User> findByUsername(String username);


}
