package com.endava.internship.infrastructure.mapper;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.endava.internship.infrastructure.domain.ParkingLot;
import com.endava.internship.infrastructure.domain.Role;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.infrastructure.domain.WorkingTime;
import com.endava.internship.web.dto.UserToParkingLotDto;
import com.endava.internship.web.dto.UserUpdatedRoleResponse;
import com.endava.internship.web.dto.WorkingTimeDto;

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
}