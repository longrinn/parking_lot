package com.endava.internship.infrastructure.mapper;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.endava.internship.infrastructure.domain.ParkingLevel;
import com.endava.internship.infrastructure.domain.ParkingLot;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.infrastructure.domain.WorkingTime;
import com.endava.internship.web.dto.ParkingLevelDetailsDto;
import com.endava.internship.web.dto.ParkingLotDto;
import com.endava.internship.web.dto.UserToParkingLotDto;
import com.endava.internship.web.dto.UserUpdatedRoleDto;
import com.endava.internship.web.dto.WorkingTimeDto;

@Mapper(componentModel = "spring")
public interface DtoMapper {

    UserUpdatedRoleDto map(User user);

    Set<WorkingTimeDto> mapWorkingTimes(Set<WorkingTime> workingTime);

    @Mapping(source = "email", target = "userEmail")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "parkingLot.name", target = "parkingLotName")
    @Mapping(source = "parkingLot.address", target = "parkingLotAddress")
    UserToParkingLotDto map(User user, ParkingLot parkingLot, String email);

    ParkingLotDto map(ParkingLot parkingLot);

    ParkingLevelDetailsDto map(ParkingLevel parkingLevel);

    Set<ParkingLevelDetailsDto> map(Set<ParkingLevel> parkingLevels);
}