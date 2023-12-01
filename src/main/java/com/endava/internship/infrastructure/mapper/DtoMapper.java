package com.endava.internship.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.endava.internship.infrastructure.domain.ParkingLot;
import com.endava.internship.infrastructure.domain.ParkingLevel;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.web.dto.UserToParkingLotDto;
import com.endava.internship.infrastructure.domain.WorkingTime;
import com.endava.internship.web.dto.ParkingLevelDto;
import com.endava.internship.web.dto.UserUpdatedRoleResponse;
import com.endava.internship.web.dto.WorkingTimeDto;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface DtoMapper {

    UserUpdatedRoleResponse map(User user);

    WorkingTime map(WorkingTimeDto workingTimeDto);

    ParkingLevel map(ParkingLevelDto parkingLevelDto);

    Set<WorkingTimeDto> mapWorkingTimes(Set<WorkingTime> workingTime);

    Set<ParkingLevelDto> mapParkingLevels(Set<ParkingLevel> parkingLevel);

    @Mapping(source = "email", target = "userEmail")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "parkingLot.name", target = "parkingLotName")
    @Mapping(source = "parkingLot.address", target = "parkingLotAddress")
    UserToParkingLotDto map(User user, ParkingLot parkingLot, String email);
}