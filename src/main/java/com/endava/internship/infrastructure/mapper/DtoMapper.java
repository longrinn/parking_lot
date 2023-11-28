package com.endava.internship.infrastructure.mapper;

import java.util.Set;

import org.mapstruct.Mapper;

import com.endava.internship.infrastructure.domain.ParkingLevel;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.infrastructure.domain.WorkingTime;
import com.endava.internship.web.dto.ParkingLevelDto;
import com.endava.internship.web.dto.UserUpdatedRoleResponse;
import com.endava.internship.web.dto.WorkingTimeDto;

@Mapper(componentModel = "spring")
public interface DtoMapper {

    UserUpdatedRoleResponse map(User user);

    WorkingTime map(WorkingTimeDto workingTimeDto);

    ParkingLevel map(ParkingLevelDto parkingLevelDto);

    Set<WorkingTimeDto> mapWorkingTimes(Set<WorkingTime> workingTime);

    Set<ParkingLevelDto> mapParkingLevels(Set<ParkingLevel> parkingLevel);

}