package com.endava.internship.infrastructure.mapper;

import org.mapstruct.Mapper;

import com.endava.internship.dao.entity.CredentialsEntity;
import com.endava.internship.dao.entity.ParkingLevelEntity;
import com.endava.internship.dao.entity.ParkingLotEntity;
import com.endava.internship.dao.entity.ParkingSpotEntity;
import com.endava.internship.dao.entity.RoleEntity;
import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.dao.entity.WorkingTimeEntity;
import com.endava.internship.infrastructure.domain.Credentials;
import com.endava.internship.infrastructure.domain.ParkingLevel;
import com.endava.internship.infrastructure.domain.ParkingLot;
import com.endava.internship.infrastructure.domain.ParkingSpot;
import com.endava.internship.infrastructure.domain.Role;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.infrastructure.domain.WorkingTime;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface DaoMapper {

    Role map(RoleEntity roleEntity);

    CredentialsEntity map(Credentials credentials);

    UserEntity map(User user);

    User map(UserEntity user);

    RoleEntity map(Role role);

    ParkingLotEntity map(ParkingLot parkingLot);

    WorkingTimeEntity map(WorkingTime workingTime);

    ParkingLevelEntity map(ParkingLevel parkingLevel);

    ParkingLot map(ParkingLotEntity parkingLotEntity);

    ParkingSpotEntity map(ParkingSpot parkingSpot);

    ParkingLevel map(ParkingLevelEntity parkingLevelEntity1);

    Set<WorkingTime> map(Set<WorkingTimeEntity> workingTimeEntities);
}