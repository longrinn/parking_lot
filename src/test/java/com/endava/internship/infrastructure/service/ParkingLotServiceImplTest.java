package com.endava.internship.infrastructure.service;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.endava.internship.dao.entity.CredentialsEntity;
import com.endava.internship.dao.entity.ParkingLotEntity;
import com.endava.internship.dao.entity.RoleEntity;
import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.dao.repository.ParkingLotRepository;
import com.endava.internship.dao.repository.UserRepository;
import com.endava.internship.infrastructure.exception.EntityAlreadyLinkedException;
import com.endava.internship.infrastructure.listeners.UserLinkToParkLotListener;
import com.endava.internship.infrastructure.mapper.DaoMapper;
import com.endava.internship.infrastructure.mapper.DtoMapper;
import com.endava.internship.web.dto.ParkingLevelDto;
import com.endava.internship.web.dto.WorkingTimeDto;
import com.endava.internship.web.request.CreateParkingLotRequest;
import com.endava.internship.web.request.LinkToParkLotRequest;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingLotServiceImplTest {

    @InjectMocks
    ParkingLotServiceImpl parkingLotService;

    @Mock
    ParkingLotRepository parkingLotRepository;

    @Mock
    UserRepository userRepository;
    @Mock
    UserLinkToParkLotListener userLinkToParkLotListener;

    @Mock
    private DaoMapper daoMapper;

    @Mock
    private DtoMapper dtoMapper;


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


    @Test
    void linkUserToParkingLotSuccessful() {
        LinkToParkLotRequest request = new LinkToParkLotRequest("user@example.com", "ParkingLotName");
        RoleEntity roleEntity = new RoleEntity(1, "User");
        CredentialsEntity credentialsEntity = new CredentialsEntity(1, null, "user@example.com", "Password");
        UserEntity userEntity = new UserEntity(1, credentialsEntity, "John", "868521164", roleEntity, null, new HashSet<>());
        ParkingLotEntity parkingLotEntity = new ParkingLotEntity(1, "ParkingLotName", "address", LocalTime.of(8, 0), LocalTime.of(20, 0), false, null, null, null);

        when(userRepository.findByCredential_Email(request.getUserEmail())).thenReturn(Optional.of(userEntity));
        when(parkingLotRepository.findByName(request.getParkingLotName())).thenReturn(Optional.of(parkingLotEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        parkingLotService.linkUserToParkingLot(request);

        verify(userRepository).findByCredential_Email(request.getUserEmail());
        verify(parkingLotRepository).findByName(request.getParkingLotName());
        verify(userRepository).save(userEntity);
    }

    @Test
    void linkNonExistentUserToParkingLot_throwsEntityNotFoundException() {
        LinkToParkLotRequest request = new LinkToParkLotRequest("user@example.com", "ParkingLotName");

        when(userRepository.findByCredential_Email(request.getUserEmail())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> parkingLotService.linkUserToParkingLot(request));
    }

    @Test
    void linkUserToNonExistentParkingLot_throwsEntityLotNotFoundException() {
        LinkToParkLotRequest request = new LinkToParkLotRequest("user@example.com", "ParkingLotName");
        RoleEntity roleEntity = new RoleEntity(1, "User");
        UserEntity userEntity = new UserEntity(1, null, "John", "868521164", roleEntity, null, null);

        when(userRepository.findByCredential_Email(request.getUserEmail())).thenReturn(Optional.of(userEntity));
        when(parkingLotRepository.findByName(request.getParkingLotName())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> parkingLotService.linkUserToParkingLot(request));
    }

    @Test
    void linkUserToParkingLotEntitiesMoreTimes_throwsEntityAlreadyLinkedException() {
        LinkToParkLotRequest request = new LinkToParkLotRequest("user@example.com", "ParkingLotName");
        RoleEntity roleEntity = new RoleEntity(1, "User");
        CredentialsEntity credentialsEntity = new CredentialsEntity(1, null, "user@example.com", "Password");
        UserEntity userEntity = new UserEntity(1, credentialsEntity, "John", "868521164", roleEntity, null, new HashSet<>());
        ParkingLotEntity parkingLotEntity = new ParkingLotEntity(1, "ParkingLotName", "address", LocalTime.of(8, 0), LocalTime.of(20, 0), false, null, null, null);

        when(userRepository.findByCredential_Email(request.getUserEmail())).thenReturn(Optional.of(userEntity));
        when(parkingLotRepository.findByName(request.getParkingLotName())).thenReturn(Optional.of(parkingLotEntity));

        userEntity.getParkingLots().add(parkingLotEntity);

        assertThrows(EntityAlreadyLinkedException.class, () -> parkingLotService.linkUserToParkingLot(request));
    }
}