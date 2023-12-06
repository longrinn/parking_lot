package com.endava.internship.web.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.endava.internship.dao.repository.ParkingLotRepository;
import com.endava.internship.infrastructure.security.JwtUtils;
import com.endava.internship.infrastructure.security.filter.JwtAuthenticationFilter;
import com.endava.internship.infrastructure.service.api.ParkingLotService;
import com.endava.internship.web.dto.ParkingLevelDto;
import com.endava.internship.web.dto.ParkingLotDetailsDto;
import com.endava.internship.web.dto.ParkingLotDto;
import com.endava.internship.web.dto.ResponseDto;
import com.endava.internship.web.dto.UserToParkingLotDto;
import com.endava.internship.web.dto.WorkingTimeDto;
import com.endava.internship.web.request.GetSpecificParkingLotRequest;
import com.endava.internship.web.request.ParkingLotRequest;
import com.endava.internship.web.request.UpdateParkLotLinkRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.time.LocalTime.MIDNIGHT;
import static java.time.LocalTime.NOON;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParkingLotController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ParkingLotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ParkingLotService parkingLotService;

    @MockBean
    private ParkingLotRepository parkingLotRepository;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser
    void linkUserToParkLotShouldReturnOkStatus() throws Exception {
        UpdateParkLotLinkRequest updateParkLotLinkRequest = new UpdateParkLotLinkRequest("userEmail@example.com", "parkingLotName");
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
    @WithMockUser
    void unlinkUserFromParkLot_ShouldReturnOkStatus() throws Exception {
        UpdateParkLotLinkRequest request = new UpdateParkLotLinkRequest("test@example.com", "ParkingLot From Test");
        ResponseDto response = new ResponseDto("Parking Lot was created with success!");

        when(parkingLotService.unlinkUserFromParkingLot(any(UpdateParkLotLinkRequest.class))).thenReturn(response);

        mockMvc.perform(delete("/link-park-lot")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andReturn();

        verify(parkingLotService).unlinkUserFromParkingLot(any(UpdateParkLotLinkRequest.class));
    }

    @Test
    void getAllParkingLots_ShouldReturnListOfParkingLots() throws Exception {
        ParkingLotDetailsDto firstParkingLotDto = new ParkingLotDetailsDto(1, "ParkingLot1","ParkingLotAddress", NOON, MIDNIGHT, null, true, 3, 2);
        ParkingLotDetailsDto secondParkingLotDto = new ParkingLotDetailsDto(1, "ParkingLot2","ParkingLotAddress", NOON, MIDNIGHT, null, true, 3, 2);

        List<ParkingLotDetailsDto> response = Arrays.asList(firstParkingLotDto, secondParkingLotDto);

        when(parkingLotService.getAllParkingLots()).thenReturn(response);

        mockMvc.perform(get("/parking-lots"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(parkingLotService).getAllParkingLots();
    }

    @Test
    void getSpecificParkingLot_ShouldReturnTheParkingLot() throws Exception {
        final GetSpecificParkingLotRequest request = new GetSpecificParkingLotRequest("user@mail.com", 1);
        final Integer id = 1;
        ParkingLotDto response = new ParkingLotDto(1, "Parking1", "address", NOON, MIDNIGHT, true, null, null);

        when(parkingLotService.getSpecificParkingLot(any(GetSpecificParkingLotRequest.class))).thenReturn(response);

        mockMvc.perform(get("/parking-lot")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(parkingLotService).getSpecificParkingLot(any(GetSpecificParkingLotRequest.class));
    }

    @Test
    void deleteExistingParkingLot_ShouldReturnStatusOK() throws Exception {
        Integer parkingLotId = 1;

        ResponseDto expected = new ResponseDto("The parking lot with ID: " + parkingLotId + " and all its related entities has been deleted");
        when(parkingLotService.deleteParkingLot(parkingLotId)).thenReturn(expected);

        mockMvc.perform(delete("/parking-lot/" + parkingLotId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));

        verify(parkingLotService).deleteParkingLot(parkingLotId);
    }

    @Test
    void createParkingLot_ShouldReturnCreatedStatus() throws Exception {
        final WorkingTimeDto workingTimeDto = new WorkingTimeDto("Monday");
        final ParkingLevelDto parkingLevelDto = new ParkingLevelDto(1, 1, 1);

        final ParkingLotRequest parkingLotRequest = new ParkingLotRequest(
                "Parking Lot From Test",
                "Random Street",
                NOON,
                NOON,
                true,
                Set.of(workingTimeDto),
                Set.of(parkingLevelDto));

        ResponseDto response = new ResponseDto("Parking Lot was created with success!");

        when(parkingLotService.createParkingLot(any(ParkingLotRequest.class))).thenReturn(response);

        mockMvc.perform(post("/parking-lot")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(parkingLotRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andReturn();

        verify(parkingLotService).createParkingLot(any(ParkingLotRequest.class));
    }

    @Test
    void updateParkingLot_ShouldReturnOkStatus() throws Exception {
        final WorkingTimeDto workingTimeDto = new WorkingTimeDto("Monday");
        final ParkingLevelDto parkingLevelDto = new ParkingLevelDto(1, 1, 1);

        ParkingLotRequest parkingLotRequest = new ParkingLotRequest("Name", "Address", MIDNIGHT, NOON, true, Set.of(workingTimeDto), Set.of(parkingLevelDto));

        ResponseDto response = new ResponseDto("Parking lot has been edited successfully");

        when(parkingLotService.updateParkingLot(anyInt(), any())).thenReturn(response);

        mockMvc.perform(put("/parking-lots/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parkingLotRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(parkingLotService).updateParkingLot(any(), any());
    }
}