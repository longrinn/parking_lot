package com.endava.internship.dao.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HealthCheckConfiguration {

    private static final String AVAILABLE = "available";
    private static final String UNAVAILABLE = "unavailable";
    private static final String DATABASE = "database";

    private final String username;
    private final String password;
    private final String url;

    public HealthCheckConfiguration(
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password,
            @Value("${spring.datasource.url}") String url) {
        this.username = username;
        this.password = password;
        this.url = url;
    }

    @Bean
    public HealthIndicator databaseHealthIndicator() {
        return () -> {
            final String databaseStatus = checkDatabaseStatus();
            if (databaseStatus.equals(AVAILABLE)) {
                return Health.up().withDetail(DATABASE, AVAILABLE).build();
            } else return Health.down().withDetail(DATABASE, UNAVAILABLE).build();
        };
    }

    private String checkDatabaseStatus() {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            if (connection.isValid(1)) {
                return AVAILABLE;
            } else return UNAVAILABLE;
        } catch (SQLException e) {
            return UNAVAILABLE;
        }
    }
}