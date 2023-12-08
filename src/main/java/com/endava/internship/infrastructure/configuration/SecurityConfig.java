package com.endava.internship.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.endava.internship.infrastructure.security.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final String[] swaggerWhiteList = {"/api-docs", "/api-docs.yaml", "/swagger", "/api-docs/swagger-config",
            "/swagger-ui/*", "/swagger-initializer", "/swagger_ui_bundle"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer ->
                        configurer
                                .sessionCreationPolicy(STATELESS)
                )
                .authorizeHttpRequests(request -> request.requestMatchers("/registration", "/authentication").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers("/parking-lot", "/link-park-lot", "/spot/{id}").hasAuthority("Admin"))
                .authorizeHttpRequests(request -> request.requestMatchers(swaggerWhiteList).permitAll())
                .authorizeHttpRequests(request -> request.anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}