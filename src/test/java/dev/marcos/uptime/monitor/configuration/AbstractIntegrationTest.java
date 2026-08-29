package dev.marcos.uptime.monitor.configuration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest // Pull all spring context/beans.
@Testcontainers // Enable Junit 5 for test containers
public abstract class AbstractIntegrationTest {
    // An abstract class configuration for integration tests

    @Container //  The Identifier of container for @TestContainer know where is the container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("db_uptime_monitor")
            .withUsername("marcos")
            .withPassword("marcos123");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}