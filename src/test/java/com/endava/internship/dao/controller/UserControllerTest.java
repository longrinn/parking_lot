package com.endava.internship.dao.controller;

import com.endava.internship.infrastructure.security.filter.JwtAuthenticationFilter;
import com.endava.internship.infrastructure.service.api.UserService;
import com.endava.internship.web.controller.UserController;
import com.endava.internship.web.dto.RoleDto;
import com.endava.internship.web.dto.UserUpdatedRoleResponse;
import com.endava.internship.web.request.ChangeRoleRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    void updateUserRoleShouldReturnOkResponse() throws Exception {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(1, "User");
        RoleDto role = new RoleDto("Admin");
        UserUpdatedRoleResponse response = new UserUpdatedRoleResponse("username@gmail.com", role);

        when(userService.updateUserRole(any())).thenReturn(response);

        mockMvc.perform(patch("/update-role")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(changeRoleRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andReturn();

        verify(userService).updateUserRole(any());
    }
}
