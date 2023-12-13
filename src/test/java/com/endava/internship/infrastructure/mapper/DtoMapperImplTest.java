package com.endava.internship.infrastructure.mapper;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.endava.internship.infrastructure.domain.ParkingLevel;
import com.endava.internship.infrastructure.domain.ParkingLot;
import com.endava.internship.infrastructure.domain.ParkingSpot;
import com.endava.internship.infrastructure.domain.Role;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.infrastructure.domain.WorkingTime;
import com.endava.internship.web.dto.ParkingLevelDetailsDto;
import com.endava.internship.web.dto.ParkingLotDto;
import com.endava.internship.web.dto.UserToParkingLotDto;
import com.endava.internship.web.dto.UserUpdatedRoleResponse;
import com.endava.internship.web.dto.WorkingTimeDto;

import static java.time.LocalTime.MIDNIGHT;
import static java.time.LocalTime.NOON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
class DtoMapperImplTest {

    private final DtoMapper dtoMapper = Mappers.getMapper(DtoMapper.class);

    @Test
    void testMap() {
        User user = User.builder()
                .id(1)
                .name("User Name")
                .phone("123456789")
                .role(new Role(2,"User"))
                .build();

        UserUpdatedRoleResponse response = dtoMapper.map(user);

        assertEquals(user.getName(), response.getName());
        assertEquals(user.getRole().getName(), response.getRole().getName());
    }

    @Test
    void testMapWithNullInput() {
        UserUpdatedRoleResponse result = dtoMapper.map((User) null);

        assertNull(result);
    }

    @Test
    void testMapWithNullRole() {
        User user = User.builder()
                .id(1)
                .name("User Name")
                .phone("123456789")
                .role(null)
                .build();

        UserUpdatedRoleResponse response = dtoMapper.map(user);

        assertNull(response.getRole());
    }

    @Test
    void testWorkingTimeDto_WhenSetWorkingTimeIsNull_ShouldReturnNullSetWorkingTimeDto() {
        Set<WorkingTime> workingTime = new HashSet<>();
        workingTime.add(null);

        Set<WorkingTimeDto> workingTimeDtos = dtoMapper.mapWorkingTimes(workingTime);
        for (WorkingTimeDto workingTimeDto : workingTimeDtos) {
            assertNull(workingTimeDto);
        }
    }

    @Test
    void testWorkingTimeDto_WhenSetWorkingTimeContainsNull_ShouldReturnNullSetWorkingTimeDto() {
        Set<WorkingTimeDto> workingTimeDtos = dtoMapper.mapWorkingTimes(null);

        assertNull(workingTimeDtos);
    }

    @Test
    void testSetWorkingTimeDto_WhenSetWorkingTimeIsValid_ShouldReturnSetWorkingTimeDto() {
        WorkingTime workingTime = WorkingTime.builder()
                .nameDay("monday")
                .build();

        Set<WorkingTimeDto> workingTimeDtos = dtoMapper.mapWorkingTimes(Set.of(workingTime));
        WorkingTimeDto workingTimeDto = workingTimeDtos.iterator().next();

        assertEquals("monday", workingTimeDto.getNameDay());
    }

    @Test
    void testMap_WhenAllParametersAreNotNull_ShouldReturnUserToParkingLotDto() {
        User user = User.builder()
                .id(1)
                .name("User Name")
                .phone("123456789")
                .build();
        ParkingLot parkingLot = ParkingLot.builder()
                .id(1)
                .name("Parking Lot from test")
                .address("Random Street")
                .startTime(LocalTime.of(6, 0, 0))
                .endTime(LocalTime.of(21, 0, 0))
                .state(true)
                .build();

        UserToParkingLotDto userToParkingLotDto = dtoMapper.map(user, parkingLot, "test@example.com");

        assertEquals(userToParkingLotDto.getUserName(), user.getName());
        assertEquals(userToParkingLotDto.getParkingLotName(), parkingLot.getName());
        assertEquals(userToParkingLotDto.getUserEmail(), "test@example.com");
    }

    @Test
    void testMap_WhenAllParametersAreNull_ShouldReturnNullUserToParkingLotDto() {
        UserToParkingLotDto userToParkingLotDto = dtoMapper.map(null, null, null);

        assertNull(userToParkingLotDto);
    }

    @Test
    void testMapParkingLotDto_WhenParkingLotIsNull_ShouldReturnNullParkingLotDto() {
        ParkingLotDto parkingLotDto = dtoMapper.map((ParkingLot) null);

        assertNull(parkingLotDto);
    }

    @Test
    void testMapParkingLotDto_WhenParkingLotIsValid_ShouldReturnParkingLotDto() {
        User user = User.builder()
                .id(1)
                .name("name")
                .build();
        ParkingLot parkingLot = new ParkingLot(1, "name", "address", NOON, MIDNIGHT, true, Set.of(user));

        ParkingLotDto parkingLotDto = dtoMapper.map(parkingLot);

        assertEquals(parkingLot.getId(), parkingLotDto.getId());
        assertEquals(parkingLot.getName(), parkingLotDto.getName());
        assertEquals(parkingLot.getAddress(), parkingLotDto.getAddress());
        assertEquals(parkingLot.getStartTime(), parkingLotDto.getStartTime());
        assertEquals(parkingLot.getEndTime(), parkingLotDto.getEndTime());
        assertEquals(parkingLot.isState(), parkingLotDto.isState());
    }

    @Test
    void testMapParkingLevelDetailsDto_WhenParkingLevelDetailsIsValid_ShouldReturnDto() {
        ParkingLevel parkingLevel = ParkingLevel.builder()
                .id(1)
                .floor(1)
                .totalSpots(1)
                .build();

        ParkingLevelDetailsDto parkingLevelDetailsDto = dtoMapper.map(parkingLevel);

        assertEquals(parkingLevel.getId(), parkingLevelDetailsDto.getId());
        assertEquals(parkingLevel.getFloor(), parkingLevelDetailsDto.getFloor());
        assertEquals(parkingLevel.getTotalSpots(), parkingLevelDetailsDto.getTotalSpots());
    }

    @Test
    void testMapParkingLevelDetailsDto_WhenParkingLevelIsNull_ShouldReturnNullParkingLevelDetailsDto() {
        ParkingLevelDetailsDto parkingLevelDetailsDto = dtoMapper.map((ParkingLevel) null);

        assertNull(parkingLevelDetailsDto);
    }

    @Test
    void testMapSetParkingLevelDetailsDto_WhenSetParkingLevelIsValid_ShouldReturnSetDto() {
        ParkingLevel parkingLevel = ParkingLevel.builder()
                .id(1)
                .floor(1)
                .totalSpots(1)
                .build();

        Set<ParkingLevelDetailsDto> parkingLevelDetailsDtoSet = dtoMapper.map(Set.of(parkingLevel));
        ParkingLevelDetailsDto parkingLevelDetailsDto = parkingLevelDetailsDtoSet.iterator().next();

        assertEquals(parkingLevelDetailsDto.getId(), parkingLevel.getId());
        assertEquals(parkingLevelDetailsDto.getFloor(), parkingLevel.getFloor());
        assertEquals(parkingLevelDetailsDto.getTotalSpots(), parkingLevel.getTotalSpots());
    }

    @Test
    void testMapSetParkingLevelDetailsDto_WhenSetParkingLevelAndSpotIsValid_ShouldReturnSetDto() {
        ParkingSpot parkingSpot = ParkingSpot.builder()
                .type("Type")
                .available(true)
                .build();

        ParkingLevel parkingLevel = ParkingLevel.builder()
                .id(1)
                .floor(1)
                .parkingSpots(Set.of(parkingSpot))
                .totalSpots(1)
                .build();

        Set<ParkingLevelDetailsDto> parkingLevelDetailsDtoSet = dtoMapper.map(Set.of(parkingLevel));
        ParkingLevelDetailsDto parkingLevelDetailsDto = parkingLevelDetailsDtoSet.iterator().next();

        assertEquals(parkingLevelDetailsDto.getId(), parkingLevel.getId());
        assertEquals(parkingLevelDetailsDto.getFloor(), parkingLevel.getFloor());
        assertEquals(parkingLevelDetailsDto.getTotalSpots(), parkingLevel.getTotalSpots());
    }

    @Test
    void testMapSetParkingLevelDetailsDto_WhenSetParkingLevelContainsNullSpot_ShouldReturnSetDto1() {
        Set<ParkingSpot> parkingSpot = null;

        ParkingLevel parkingLevel = ParkingLevel.builder()
                .id(1)
                .floor(1)
                .parkingSpots(parkingSpot)
                .totalSpots(1)
                .build();

        Set<ParkingLevelDetailsDto> parkingLevelDetailsDtoSet = dtoMapper.map(Set.of(parkingLevel));
        ParkingLevelDetailsDto parkingLevelDetailsDto = parkingLevelDetailsDtoSet.iterator().next();

        assertEquals(parkingLevelDetailsDto.getId(), parkingLevel.getId());
        assertEquals(parkingLevelDetailsDto.getFloor(), parkingLevel.getFloor());
        assertEquals(parkingLevelDetailsDto.getTotalSpots(), parkingLevel.getTotalSpots());
        assertNull(parkingLevel.getParkingSpots());
    }

    @Test
    void testMapSetParkingLevelDetailsDto_WhenSetParkingLevelSetIsNull_ShouldReturnNull() {
        Set<ParkingLevelDetailsDto> parkingLevelDetailsDtoSet = dtoMapper.map((Set<ParkingLevel>) null);

        assertNull(parkingLevelDetailsDtoSet);
    }
}