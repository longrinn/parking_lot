package com.endava.internship.web.controller;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.endava.internship.infrastructure.security.filter.JwtAuthenticationFilter;
import com.endava.internship.infrastructure.service.ParkingSpotServiceImpl;
import com.endava.internship.web.dto.ResponseDto;
import com.endava.internship.web.request.UpdateParkingSpotRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParkingSpotController.class)
@AutoConfigureMockMvc(addFilters = false)
class ParkingSpotControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ParkingSpotServiceImpl parkingSpotService;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
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
}