package com.endava.internship.infrastructure.service;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.endava.internship.dao.entity.CredentialsEntity;
import com.endava.internship.dao.entity.ParkingLevelEntity;
import com.endava.internship.dao.entity.ParkingLotEntity;
import com.endava.internship.dao.entity.ParkingSpotEntity;
import com.endava.internship.dao.entity.RoleEntity;
import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.dao.entity.WorkingTimeEntity;
import com.endava.internship.dao.repository.ParkingLevelRepository;
import com.endava.internship.dao.repository.ParkingLotRepository;
import com.endava.internship.dao.repository.UserRepository;
import com.endava.internship.dao.repository.WorkingTimeRepository;
import com.endava.internship.infrastructure.domain.ParkingLevel;
import com.endava.internship.infrastructure.domain.ParkingLot;
import com.endava.internship.infrastructure.domain.Role;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.infrastructure.domain.WorkingTime;
import com.endava.internship.infrastructure.exception.EntityAlreadyLinkedException;
import com.endava.internship.infrastructure.exception.EntityAreNotLinkedException;
import com.endava.internship.infrastructure.listeners.UserLinkToParkLotListener;
import com.endava.internship.infrastructure.listeners.UserUnlinkFromParkingLotListener;
import com.endava.internship.infrastructure.mapper.DaoMapper;
import com.endava.internship.infrastructure.mapper.DtoMapper;
import com.endava.internship.web.dto.ParkingLevelDto;
import com.endava.internship.web.dto.ParkingLotDetailsDto;
import com.endava.internship.web.dto.UnlinkUserDto;
import com.endava.internship.web.dto.UserToParkingLotDto;
import com.endava.internship.web.dto.WorkingTimeDto;
import com.endava.internship.web.request.CreateParkingLotRequest;
import com.endava.internship.web.request.UpdateParkLotLinkRequest;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import static java.time.LocalTime.MIDNIGHT;
import static java.time.LocalTime.NOON;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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

    @Mock
    ParkingLevelRepository parkingLevelRepository;

    @Mock
    WorkingTimeRepository workingTimeRepository;

    @Mock
    UserUnlinkFromParkingLotListener userUnlinkFromParkingLotListener;

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
        UpdateParkLotLinkRequest request = new UpdateParkLotLinkRequest("user@example.com", "ParkingLotName");
        RoleEntity roleEntity = new RoleEntity(1, "User");
        CredentialsEntity credentialsEntity = new CredentialsEntity(1, null, "user@example.com", "Password");
        UserEntity userEntity = new UserEntity(1, credentialsEntity, "John", "868521164", roleEntity, null, new HashSet<>());
        ParkingLotEntity parkingLotEntity = new ParkingLotEntity(1, "ParkingLotName", "address", LocalTime.of(8, 0), LocalTime.of(20, 0), false, null);

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
        UpdateParkLotLinkRequest request = new UpdateParkLotLinkRequest("user@example.com", "ParkingLotName");

        when(userRepository.findByCredential_Email(request.getUserEmail())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> parkingLotService.linkUserToParkingLot(request));
    }

    @Test
    void linkUserToNonExistentParkingLot_throwsEntityLotNotFoundException() {
        UpdateParkLotLinkRequest request = new UpdateParkLotLinkRequest("user@example.com", "ParkingLotName");
        RoleEntity roleEntity = new RoleEntity(1, "User");
        UserEntity userEntity = new UserEntity(1, null, "John", "868521164", roleEntity, null, null);

        when(userRepository.findByCredential_Email(request.getUserEmail())).thenReturn(Optional.of(userEntity));
        when(parkingLotRepository.findByName(request.getParkingLotName())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> parkingLotService.linkUserToParkingLot(request));
    }

    @Test
    void linkUserToParkingLotEntitiesMoreTimes_throwsEntityAlreadyLinkedException() {
        UpdateParkLotLinkRequest request = new UpdateParkLotLinkRequest("user@example.com", "ParkingLotName");
        RoleEntity roleEntity = new RoleEntity(1, "User");
        CredentialsEntity credentialsEntity = new CredentialsEntity(1, null, "user@example.com", "Password");
        UserEntity userEntity = new UserEntity(1, credentialsEntity, "John", "868521164", roleEntity, null, new HashSet<>());
        ParkingLotEntity parkingLotEntity = new ParkingLotEntity(1, "ParkingLotName", "address", LocalTime.of(8, 0), LocalTime.of(20, 0), false, null);

        when(userRepository.findByCredential_Email(request.getUserEmail())).thenReturn(Optional.of(userEntity));
        when(parkingLotRepository.findByName(request.getParkingLotName())).thenReturn(Optional.of(parkingLotEntity));

        userEntity.getParkingLots().add(parkingLotEntity);

        assertThrows(EntityAlreadyLinkedException.class, () -> parkingLotService.linkUserToParkingLot(request));
    }

    @Test
    void unlinkNonExistentUserFromParkingLot_throwsEntityNotFoundException() {
        UpdateParkLotLinkRequest request = new UpdateParkLotLinkRequest("user@example.com", "ParkingLotName");

        when(userRepository.findByCredential_Email(request.getUserEmail())).thenReturn(empty());

        assertThrows(EntityNotFoundException.class, () -> parkingLotService.unlinkUserFromParkingLot(request));
    }

    @Test
    void unlinkUserFromNonExistentParkingLot_ShouldThrowsEntityLotNotFoundException() {
        final UpdateParkLotLinkRequest request = new UpdateParkLotLinkRequest("user@example.com", "ParkingLotName");
        final RoleEntity roleEntity = new RoleEntity(1, "User");
        final UserEntity userEntity = new UserEntity(1, null, "John", "868521164", roleEntity, null, null);

        when(userRepository.findByCredential_Email(request.getUserEmail())).thenReturn(Optional.of(userEntity));
        when(parkingLotRepository.findByName(request.getParkingLotName())).thenReturn(empty());

        assertThrows(EntityNotFoundException.class, () -> parkingLotService.unlinkUserFromParkingLot(request));
    }

    @Test
    void unlinkUserFromParkingLotSuccessful() {
        final UpdateParkLotLinkRequest request = new UpdateParkLotLinkRequest("user@example.com", "ParkingLotName");

        final RoleEntity roleEntity = new RoleEntity(1, "User");

        final CredentialsEntity credentialsEntity = CredentialsEntity.builder()
                .email("user@example.com")
                .build();

        final UserEntity userEntity = UserEntity.builder()
                .id(1)
                .credential(credentialsEntity)
                .name("John")
                .role(roleEntity)
                .parkingLots(new HashSet<>())
                .build();

        final Role role = new Role("User");
        final User user = new User(1, "John", "868521164", role, null);

        final ParkingLotEntity parkingLotEntity = ParkingLotEntity.builder()
                .id(1)
                .name("ParkingLotName")
                .address("address")
                .users(new HashSet<>())
                .build();
        parkingLotEntity.getUsers().add(userEntity);

        final Set<ParkingLotEntity> parkingLotEntities = new HashSet<>();
        parkingLotEntities.add(parkingLotEntity);
        userEntity.setParkingLots(parkingLotEntities);

        when(userRepository.findByCredential_Email(request.getUserEmail())).thenReturn(Optional.of(userEntity));
        when(parkingLotRepository.findByName(request.getParkingLotName())).thenReturn(Optional.of(parkingLotEntity));
        doNothing().when(userUnlinkFromParkingLotListener).handleUserUnlinkFromParkLotEvent(any(UserToParkingLotDto.class));
        when(daoMapper.map(userEntity)).thenReturn(user);

        UnlinkUserDto result = parkingLotService.unlinkUserFromParkingLot(request);
        UnlinkUserDto unlinkUserDto = new UnlinkUserDto("John has been unlinked");

        verify(userRepository).findByCredential_Email(request.getUserEmail());
        verify(parkingLotRepository).findByName(request.getParkingLotName());

        assertFalse(userEntity.getParkingLots().contains(parkingLotEntity));
        assertNotNull(result);
        assertEquals(unlinkUserDto.getMessage(), result.getMessage());
    }

    @Test
    void unlinkUserFromParkingLotNotLinked_ShouldThrowEntityAreNotLinkedException() {
        final UpdateParkLotLinkRequest request = new UpdateParkLotLinkRequest("user@example.com", "ParkingLotName");
        final RoleEntity roleEntity = new RoleEntity(1, "User");
        final CredentialsEntity credentialsEntity = new CredentialsEntity(1, null, "user@example.com", "Password");
        final UserEntity userEntity = new UserEntity(1, credentialsEntity, "John", "868521164", roleEntity, null, new HashSet<>());
        final ParkingLotEntity parkingLotEntity = new ParkingLotEntity(1, "ParkingLotName", "address", LocalTime.of(8, 0), LocalTime.of(20, 0), false, null);

        when(userRepository.findByCredential_Email(request.getUserEmail())).thenReturn(Optional.of(userEntity));
        when(parkingLotRepository.findByName(request.getParkingLotName())).thenReturn(Optional.of(parkingLotEntity));

        assertThrows(EntityAreNotLinkedException.class, () -> parkingLotService.unlinkUserFromParkingLot(request));
    }

    @Test
    public void testGetAllParkingLots_ShouldReturnAllParkingLots() {
        // Constants
        final String nameOfParkingLot = "Name";
        final String addressOfParkingLot = "Address";
        final String monday = "Monday";

        // Entities
        final CredentialsEntity credentialsEntity = CredentialsEntity.builder()
                .id(1)
                .email("user@mail.com")
                .password("password")
                .build();
        final RoleEntity roleEntity = new RoleEntity(1, "Admin");
        final UserEntity userEntity = UserEntity.builder()
                .id(1)
                .credential(credentialsEntity)
                .name("username")
                .phone("063251148")
                .role(roleEntity)
                .build();
        final ParkingLotEntity parkingLotEntity = new ParkingLotEntity(1, nameOfParkingLot, addressOfParkingLot, NOON, MIDNIGHT, true, Set.of(userEntity));
        final ParkingSpotEntity parkingSpotEntity = new ParkingSpotEntity(1, null, "1spot", false, "regular", userEntity);
        final ParkingLevelEntity parkingLevelEntity = ParkingLevelEntity.builder()
                .id(1)
                .floor(1)
                .totalSpots(1)
                .parkingSpots(Set.of(parkingSpotEntity))
                .build();
        final WorkingTimeEntity workingTimeEntity = new WorkingTimeEntity();
        workingTimeEntity.setId(1);
        workingTimeEntity.setNameDay(monday);

        // Domain
        final WorkingTime workingTime = WorkingTime.builder()
                .nameDay(monday)
                .build();
        final ParkingLevel parkingLevel = ParkingLevel.builder()
                .id(1)
                .floor(1)
                .totalSpots(1)
                .build();
        final ParkingLot parkingLot = new ParkingLot(1, nameOfParkingLot, addressOfParkingLot, NOON, NOON, true, Set.of(workingTime), Set.of(parkingLevel), Set.of(userEntity));

        // Dto
        final WorkingTimeDto workingTimeDto = new WorkingTimeDto("Monday");
        final ParkingLotDetailsDto parkingLotDetailsDto = new ParkingLotDetailsDto(nameOfParkingLot, NOON, NOON, Set.of(workingTimeDto), true, 1, 1);

        when(parkingLotRepository.findAll()).thenReturn(List.of(parkingLotEntity));
        when(daoMapper.map(any(ParkingLotEntity.class))).thenReturn(parkingLot);
        when(parkingLevelRepository.getByParkingLotId(any())).thenReturn(List.of(parkingLevelEntity));
        when(workingTimeRepository.findByParkingLot_Id(any())).thenReturn(Optional.of(Set.of(workingTimeEntity)));
        when(daoMapper.map(ArgumentMatchers.<Set<WorkingTimeEntity>>any())).thenReturn(Set.of(workingTime));
        when(dtoMapper.mapWorkingTimes(any())).thenReturn(Set.of(workingTimeDto));

        final List<ParkingLotDetailsDto> result = parkingLotService.getAllParkingLots();

        verify(parkingLotRepository).findAll();
        verify(daoMapper).map(any(ParkingLotEntity.class));
        verify(parkingLevelRepository).getByParkingLotId(any());
        verify(workingTimeRepository).findByParkingLot_Id(any());
        verify(daoMapper).map(ArgumentMatchers.<Set<WorkingTimeEntity>>any());
        verify(dtoMapper).mapWorkingTimes(any());

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(parkingLotDetailsDto);
    }
}