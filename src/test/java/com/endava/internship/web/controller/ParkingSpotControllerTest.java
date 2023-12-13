package com.endava.internship.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.endava.internship.dao.entity.CredentialsEntity;
import com.endava.internship.dao.entity.RoleEntity;
import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.infrastructure.security.JwtUtils;
import com.endava.internship.infrastructure.security.UserDetailsImpl;
import com.endava.internship.infrastructure.service.api.ParkingSpotService;
import com.endava.internship.web.dto.ParkingSpotDto;
import com.endava.internship.web.dto.ResponseDto;
import com.endava.internship.web.request.SpotOccupancyRequest;
import com.endava.internship.web.request.UpdateParkingSpotRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParkingSpotController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ParkingSpotControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private ParkingSpotService parkingSpotService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenCallEditParkingSpotType_ShouldReturnOkStatus() throws Exception {
        final ResponseDto parkingSpotDto = new ResponseDto("Parking spot type");

        when(parkingSpotService.editParkingSpot(anyInt(), any())).thenReturn(parkingSpotDto);
        UpdateParkingSpotRequest updateRequest = new UpdateParkingSpotRequest("Parking spot type");

        mockMvc.perform(put("/spot/{id}", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(parkingSpotDto)))
                .andReturn();

        verify(parkingSpotService).editParkingSpot(any(), any());
    }

    @Test
    @WithMockUser
    void occupyParkingSpot_ShouldReturnOkStatus() throws Exception {
        Integer spotId = 1;
        SpotOccupancyRequest request = new SpotOccupancyRequest("test@example.com");
        ParkingSpotDto parkingSpotDto = new ParkingSpotDto(1, "A-001", true, "New Type");

        final CredentialsEntity credentialsEntity = CredentialsEntity.builder()
                .id(1)
                .email("test@example.com")
                .password("password")
                .build();
        final RoleEntity roleEntity = new RoleEntity(2, "User");
        final UserEntity userEntity = UserEntity.builder()
                .id(1)
                .credential(credentialsEntity)
                .name("username")
                .phone("063251148")
                .role(roleEntity)
                .build();
        UserDetails userDetails = new UserDetailsImpl(userEntity);

        when(parkingSpotService.occupyParkingSpot(anyInt(), any(SpotOccupancyRequest.class), any(UserDetails.class)))
                .thenReturn(parkingSpotDto);

        mockMvc.perform(post("/spot/{spotId}", spotId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(parkingSpotDto)))
                .andReturn();

        verify(parkingSpotService).occupyParkingSpot(anyInt(), any(SpotOccupancyRequest.class), any(UserDetails.class));
    }

    @Test
    public void deleteLinkageUserSpot_ShouldReturnStatusOK() throws Exception {
        Integer spotId = 1;
        ResponseDto responseDto = new ResponseDto("Linkage successfully deleted!");

        when(parkingSpotService.deleteSpotUserLinkage(anyInt())).thenReturn(responseDto);

        mockMvc.perform(delete("/spot/link/" + spotId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        verify(parkingSpotService).deleteSpotUserLinkage(anyInt());
    }
}