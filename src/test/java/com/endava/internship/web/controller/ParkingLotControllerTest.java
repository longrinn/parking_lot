package com.endava.internship.web.controller;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.endava.internship.infrastructure.security.filter.JwtAuthenticationFilter;
import com.endava.internship.infrastructure.service.api.ParkingLotService;
import com.endava.internship.web.dto.ParkingLotDetailsDto;
import com.endava.internship.web.dto.UserToParkingLotDto;
import com.endava.internship.web.request.UpdateParkLotLinkRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.time.LocalTime.MIDNIGHT;
import static java.time.LocalTime.NOON;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParkingLotController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ParkingLotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ParkingLotService parkingLotService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser
    void linkUserToParkLotShouldReturnOkStatus() throws Exception {
        UpdateParkLotLinkRequest updateParkLotLinkRequest = new UpdateParkLotLinkRequest("parkingLotName", "userEmail@example.com");
        UserToParkingLotDto userToParkingLotDto = new UserToParkingLotDto("userEmail@example.com", "UserName", "ParkingLotName", "ParkingLotAddress");

        when(parkingLotService.linkUserToParkingLot(any())).thenReturn(userToParkingLotDto);

        mockMvc.perform(post("/link-park-lot")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateParkLotLinkRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userToParkingLotDto)))
                .andReturn();

        verify(parkingLotService).linkUserToParkingLot(any());
    }

    @Test
    void getAllParkingLots_ShouldReturnListOfParkingLots() throws Exception {
        ParkingLotDetailsDto firstParkingLotDto = new ParkingLotDetailsDto(1, "ParkingLot1", NOON, MIDNIGHT, null, true, 3, 2);
        ParkingLotDetailsDto secondParkingLotDto = new ParkingLotDetailsDto(1, "ParkingLot2", NOON, MIDNIGHT, null, true, 3, 2);

        List<ParkingLotDetailsDto> response = Arrays.asList(firstParkingLotDto, secondParkingLotDto);

        when(parkingLotService.getAllParkingLots()).thenReturn(response);

        mockMvc.perform(get("/parking-lots"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(parkingLotService).getAllParkingLots();
    }
}