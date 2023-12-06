package com.endava.internship.dao.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class HealthCheckConfigurationTest {

    @Mock
    Connection connection;
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

    @Test
    void testCheckDatabaseStatusWhenConnectionIsNotValid() throws SQLException {

        when(connection.isValid(1)).thenReturn(false);

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager
                    .when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(connection);

            HealthCheckConfiguration healthCheckConfiguration = new HealthCheckConfiguration(username, password, url);
            HealthIndicator healthIndicator = healthCheckConfiguration.databaseHealthIndicator();
            Health health = healthIndicator.health();

            assertEquals(UNAVAILABLE, health.getDetails().get(DATABASE));

        }
    }

}