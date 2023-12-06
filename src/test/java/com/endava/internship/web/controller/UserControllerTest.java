package com.endava.internship.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.endava.internship.infrastructure.security.filter.JwtAuthenticationFilter;
import com.endava.internship.infrastructure.service.api.UserService;
import com.endava.internship.web.dto.AuthenticationDto;
import com.endava.internship.web.dto.RoleDto;
import com.endava.internship.web.dto.UserUpdatedRoleDto;
import com.endava.internship.web.request.AuthenticationRequest;
import com.endava.internship.web.request.ChangeRoleRequest;
import com.endava.internship.web.request.RegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser
    void registerUserShouldReturnCreatedStatus() throws Exception {
        String email = "user@mail.com";
        RegistrationRequest request = new RegistrationRequest("UserName", email, "User1!", "067860680");

        AuthenticationDto response = AuthenticationDto.builder().email(email).role("User").jwt("token").build();

        when(userService.registration(any())).thenReturn(response);

        mockMvc.perform(post("/registration").contentType(APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated()).andExpect(content().json(objectMapper.writeValueAsString(response))).andReturn();

        verify(userService).registration(any());
    }

    @Test
    @WithMockUser
    void authenticateUserShouldReturnOkStatus() throws Exception {
        String email = "user@mail.com";
        AuthenticationRequest request = new AuthenticationRequest(email, "User1!");

        AuthenticationDto response = AuthenticationDto.builder().email(email).role("User").jwt("token").build();

        when(userService.authentication(any())).thenReturn(response);

        mockMvc.perform(post("/authentication").contentType(APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(request))).andExpect(status().isOk()).andExpect(content().json(objectMapper.writeValueAsString(response))).andReturn();

        verify(userService).authentication(any());
    }

    @Test
    @WithMockUser
    void updateUserRoleShouldReturnOkResponse() throws Exception {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest("username@gmail.com", "User");
        RoleDto role = new RoleDto("Admin");
        UserUpdatedRoleDto response = new UserUpdatedRoleDto("username@gmail.com", role);

        when(userService.updateUserRole(any())).thenReturn(response);

        mockMvc.perform(patch("/update-role").contentType(APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(changeRoleRequest))).andExpect(status().isOk()).andExpect(content().json(objectMapper.writeValueAsString(response))).andReturn();

        verify(userService).updateUserRole(any());
    }

}