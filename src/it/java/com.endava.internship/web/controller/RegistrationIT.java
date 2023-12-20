package com.endava.internship.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RegistrationIT {

    @Autowired
    MockMvc mockMvc;

    @Sql("classpath:authentication.sql")
    @Test
    public void testUserRegistration() throws Exception {
        String registrationPayload = "{"
                + "\"name\":\"Example\","
                + "\"number\":\"123456789\","
                + "\"email\":\"example@mail.com\","
                + "\"password\":\"Password.1\""
                + "}";

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationPayload))
                .andExpect(status().isCreated());
    }
}