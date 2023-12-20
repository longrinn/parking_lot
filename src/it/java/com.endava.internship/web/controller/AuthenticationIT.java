package com.endava.internship.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthenticationIT {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    MockMvc mockMvc;
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Sql("classpath:authentication.sql")
    @Test
    public void testUserAuthentication() throws Exception {
        String hashedPassword = encoder.encode("Password1.");

        jdbcTemplate.update("INSERT INTO credentials (id, email, password) VALUES (?, ?, ?)",
                100, "johndoe@example.com", hashedPassword);

        String authenticationPayload = "{"
                + "\"email\":\"johndoe@example.com\","
                + "\"password\":\"Password1.\""
                + "}";

        mockMvc.perform(post("/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticationPayload))
                .andExpect(status().isOk());
    }
}