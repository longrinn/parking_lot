package com.endava.internship.dao.entity.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.endava.internship.dao.configuration.HealthCheckConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class HealthCheckConfigurationTest {

    private static final String AVAILABLE = "available";
    private static final String UNAVAILABLE = "unavailable";
    private static final String DATABASE = "database";

    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.url}")
    private String url;

    @Test
    @DisplayName("Should pass database health check if connection is established")
    public void testDatabaseConnection() {

        final HealthCheckConfiguration healthCheckConfiguration = new HealthCheckConfiguration(username, password, url);

        final HealthIndicator healthIndicator = healthCheckConfiguration.databaseHealthIndicator();
        final Health health = healthIndicator.health();

        assertEquals(AVAILABLE, health.getDetails().get(DATABASE));
    }

    @Test
    @DisplayName("Should fail database health check if there is any connetion problem")
    public void testUnavailableDatabaseConnection() {

        final HealthCheckConfiguration healthCheckConfiguration = new HealthCheckConfiguration(username, password, "jdbc:wrong-url");

        final HealthIndicator healthIndicator = healthCheckConfiguration.databaseHealthIndicator();
        final Health health = healthIndicator.health();

        assertEquals(UNAVAILABLE, health.getDetails().get(DATABASE));
    }
}