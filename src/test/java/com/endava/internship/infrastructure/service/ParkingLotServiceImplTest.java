package com.endava.internship.infrastructure.service;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.endava.internship.dao.repository.ParkingLotRepository;
import com.endava.internship.web.dto.ParkingLevelDto;
import com.endava.internship.web.dto.WorkingTimeDto;
import com.endava.internship.web.request.CreateParkingLotRequest;

import jakarta.persistence.EntityExistsException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingLotServiceImplTest {

    @InjectMocks
    ParkingLotServiceImpl parkingLotService;

    @Mock
    ParkingLotRepository parkingLotRepository;


    @Test
    public void testCreateParkingLot_WhenParkingLotWithSameNameExists_ShouldThrow_EntityExistsException() {
        WorkingTimeDto workingTime1 = new WorkingTimeDto("Monday");
        WorkingTimeDto workingTime2 = new WorkingTimeDto("Tuesday");
        Set<WorkingTimeDto> workingTimesDto = new HashSet<>();
        workingTimesDto.add(workingTime1);
        workingTimesDto.add(workingTime2);

        ParkingLevelDto parkingLevel1 = new ParkingLevelDto(1, 47);
        ParkingLevelDto parkingLevel2 = new ParkingLevelDto(2, 36);
        Set<ParkingLevelDto> parkingLevelsDto = new HashSet<>();
        parkingLevelsDto.add(parkingLevel1);
        parkingLevelsDto.add(parkingLevel2);

        CreateParkingLotRequest parkingLotRequest = new CreateParkingLotRequest(
                "Parking Lot From Test",
                "Random Street",
                LocalTime.of(6, 0, 0),
                LocalTime.of(21, 0, 0),
                false,
                workingTimesDto,
                parkingLevelsDto);

        when(parkingLotRepository.existsByName(parkingLotRequest.getName())).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> parkingLotService.createParkingLot(parkingLotRequest));
    }
}