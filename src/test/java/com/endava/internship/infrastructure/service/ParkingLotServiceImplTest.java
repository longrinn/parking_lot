package com.endava.internship.infrastructure.service;

import java.time.DateTimeException;
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
import com.endava.internship.dao.repository.ParkingSpotRepository;
import com.endava.internship.dao.repository.UserRepository;
import com.endava.internship.dao.repository.WorkingTimeRepository;
import com.endava.internship.infrastructure.domain.ParkingLevel;
import com.endava.internship.infrastructure.domain.ParkingLot;
import com.endava.internship.infrastructure.domain.ParkingSpot;
import com.endava.internship.infrastructure.domain.Role;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.infrastructure.domain.WorkingTime;
import com.endava.internship.infrastructure.exception.EntityLinkException;
import com.endava.internship.infrastructure.exception.InvalidRequestParameterException;
import com.endava.internship.infrastructure.listeners.UserLinkToParkLotListener;
import com.endava.internship.infrastructure.listeners.UserUnlinkFromParkingLotListener;
import com.endava.internship.infrastructure.mapper.DaoMapper;
import com.endava.internship.infrastructure.mapper.DtoMapper;
import com.endava.internship.web.dto.ParkingLevelDetailsDto;
import com.endava.internship.web.dto.ParkingLevelDto;
import com.endava.internship.web.dto.ParkingLotDetailsDto;
import com.endava.internship.web.dto.ParkingLotDto;
import com.endava.internship.web.dto.ParkingSpotDtoAdmin;
import com.endava.internship.web.dto.ResponseDto;
import com.endava.internship.web.dto.UserToParkingLotDto;
import com.endava.internship.web.dto.WorkingTimeDto;
import com.endava.internship.web.request.ParkingLotRequest;
import com.endava.internship.web.request.UpdateParkLotLinkRequest;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import static java.time.LocalTime.MIDNIGHT;
import static java.time.LocalTime.NOON;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
    ParkingLevelRepository parkingLevelRepository;
    @Mock
    ParkingSpotRepository parkingSpotRepository;
    @Mock
    WorkingTimeRepository workingTimeRepository;
    @Mock
    UserUnlinkFromParkingLotListener userUnlinkFromParkingLotListener;
    @Mock
    private DaoMapper daoMapper;
    @Mock
    private DtoMapper dtoMapper;

    @Test
    public void testCreateParkingLot_WhenParkingLotWithSameNameExists_ShouldThrowEntityExistsException() {
        final String nameOfParkingLot = "Parking Lot From Test";
        final String addressOfParkingLot = "Random Street";
        final String monday = "Monday";

        final WorkingTimeDto workingTimeDto = new WorkingTimeDto(monday);
        final ParkingLevelDto parkingLevelDto = new ParkingLevelDto(1, 1, 1);

        final ParkingLotRequest parkingLotRequest = new ParkingLotRequest(
                nameOfParkingLot,
                addressOfParkingLot,
                LocalTime.of(6, 0, 0),
                LocalTime.of(21, 0, 0),
                true,
                Set.of(workingTimeDto),
                Set.of(parkingLevelDto));

        when(parkingLotRepository.existsByName(parkingLotRequest.getName())).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> parkingLotService.createParkingLot(parkingLotRequest));

        verify(parkingLotRepository).existsByName(parkingLotRequest.getName());
    }

    @Test
    public void testCreateParkingLot_WhenParkingLotWithSameAddressExists_ShouldThrowEntityExistsException() {
        final String nameOfParkingLot = "Parking Lot From Test";
        final String addressOfParkingLot = "Random Street";
        final String monday = "Monday";

        final WorkingTimeDto workingTimeDto = new WorkingTimeDto(monday);
        final ParkingLevelDto parkingLevelDto = new ParkingLevelDto(1, 1, 1);

        final ParkingLotRequest parkingLotRequest = new ParkingLotRequest(
                nameOfParkingLot,
                addressOfParkingLot,
                LocalTime.of(6, 0, 0),
                LocalTime.of(21, 0, 0),
                true,
                Set.of(workingTimeDto),
                Set.of(parkingLevelDto));

        when(parkingLotRepository.existsByAddress(parkingLotRequest.getAddress())).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> parkingLotService.createParkingLot(parkingLotRequest));

        verify(parkingLotRepository).existsByAddress(parkingLotRequest.getAddress());
    }

    @Test
    public void testCreateParkingLot_ShouldCreateParkingLotWithSuccess() {
        final String nameOfParkingLot = "Parking Lot From Test";
        final String addressOfParkingLot = "Random Street";
        final String monday = "Monday";

        final WorkingTimeDto workingTimeDto = new WorkingTimeDto(monday);
        final ParkingLevelDto parkingLevelDto = new ParkingLevelDto(1, 1, 1);

        final ParkingLotRequest parkingLotRequest = new ParkingLotRequest(
                nameOfParkingLot,
                addressOfParkingLot,
                LocalTime.of(6, 0, 0),
                LocalTime.of(21, 0, 0),
                true,
                Set.of(workingTimeDto),
                Set.of(parkingLevelDto));

        final ParkingLevel parkingLevel = ParkingLevel.builder()
                .id(1)
                .floor(1)
                .totalSpots(1)
                .build();

        final ParkingLot parkingLot = ParkingLot.builder()
                .id(1)
                .name(nameOfParkingLot)
                .address(addressOfParkingLot)
                .startTime(LocalTime.of(6, 0, 0))
                .endTime(LocalTime.of(21, 0, 0))
                .state(true)
                .build();

        final ParkingLotEntity parkingLotEntity = new ParkingLotEntity(
                1,
                nameOfParkingLot,
                addressOfParkingLot,
                LocalTime.of(6, 0, 0),
                LocalTime.of(21, 0, 0),
                true,
                null);

        final ParkingSpotEntity parkingSpotEntity = new ParkingSpotEntity(1,
                null,
                "A-001",
                false,
                "regular",
                null);

        final ParkingLevelEntity parkingLevelEntity = ParkingLevelEntity.builder()
                .id(1)
                .floor(1)
                .totalSpots(1)
                .parkingSpots(Set.of(parkingSpotEntity))
                .build();

        final WorkingTimeEntity workingTimeEntity = new WorkingTimeEntity();
        workingTimeEntity.setId(1);
        workingTimeEntity.setNameDay(monday);

        List<ParkingLevelEntity> parkingLevelEntities = List.of(parkingLevelEntity);

        when(parkingLotRepository.existsByName(parkingLotRequest.getName())).thenReturn(false);
        when(daoMapper.map(any(ParkingLot.class))).thenReturn(parkingLotEntity);
        when(parkingLotRepository.save(parkingLotEntity)).thenReturn(parkingLotEntity);
        when(daoMapper.map(any(ParkingLotEntity.class))).thenReturn(parkingLot);
        when(daoMapper.map(any(ParkingLevel.class))).thenReturn(parkingLevelEntity);
        when(parkingLevelRepository.save(parkingLevelEntity)).thenReturn(parkingLevelEntity);
        when(parkingLevelRepository.getByParkingLotId(anyInt())).thenReturn(parkingLevelEntities);
        when(daoMapper.map(any(ParkingLevelEntity.class))).thenReturn(parkingLevel);
        when(daoMapper.map(any(ParkingSpot.class))).thenReturn(parkingSpotEntity);
        when(parkingSpotRepository.save(parkingSpotEntity)).thenReturn(parkingSpotEntity);
        when(daoMapper.map(any(WorkingTime.class))).thenReturn(workingTimeEntity);
        when(workingTimeRepository.save(workingTimeEntity)).thenReturn(workingTimeEntity);

        ResponseDto response = parkingLotService.createParkingLot(parkingLotRequest);

        verify(parkingLotRepository).existsByName(parkingLotRequest.getName());
        verify(daoMapper, times(2)).map(any(ParkingLot.class));
        verify(parkingLotRepository, times(2)).save(parkingLotEntity);
        verify(daoMapper).map(any(ParkingLotEntity.class));
        verify(daoMapper).map(any(ParkingLevel.class));
        verify(parkingLevelRepository).save(parkingLevelEntity);
        verify(parkingLevelRepository).getByParkingLotId(anyInt());
        verify(daoMapper).map(any(ParkingLevelEntity.class));
        verify(daoMapper).map(any(ParkingSpot.class));
        verify(parkingSpotRepository).save(parkingSpotEntity);
        verify(daoMapper).map(any(WorkingTime.class));
        verify(workingTimeRepository).save(workingTimeEntity);

        assertEquals("Parking Lot was created with success!", response.getMessage());

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
    void linkUserToParkingLotEntitiesMoreTimes_throwsEntityLinkException() {
        UpdateParkLotLinkRequest request = new UpdateParkLotLinkRequest("user@example.com", "ParkingLotName");
        RoleEntity roleEntity = new RoleEntity(1, "User");
        CredentialsEntity credentialsEntity = new CredentialsEntity(1, null, "user@example.com", "Password");
        UserEntity userEntity = new UserEntity(1, credentialsEntity, "John", "868521164", roleEntity, null, new HashSet<>());
        ParkingLotEntity parkingLotEntity = new ParkingLotEntity(1, "ParkingLotName", "address", LocalTime.of(8, 0), LocalTime.of(20, 0), false, null);

        when(userRepository.findByCredential_Email(request.getUserEmail())).thenReturn(Optional.of(userEntity));
        when(parkingLotRepository.findByName(request.getParkingLotName())).thenReturn(Optional.of(parkingLotEntity));

        userEntity.getParkingLots().add(parkingLotEntity);

        assertThrows(EntityLinkException.class, () -> parkingLotService.linkUserToParkingLot(request));
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

        final Role role = new Role(2, "User");
        final User user = new User(1, "John", "868521164", role, null);

        final ParkingLotEntity parkingLotEntity = ParkingLotEntity.builder()
                .id(1)
                .name("ParkingLotName")
                .address("address")
                .users(new HashSet<>())
                .state(false)
                .build();
        parkingLotEntity.getUsers().add(userEntity);

        final Set<ParkingLotEntity> parkingLotEntities = new HashSet<>();
        parkingLotEntities.add(parkingLotEntity);
        userEntity.setParkingLots(parkingLotEntities);

        when(userRepository.findByCredential_Email(request.getUserEmail())).thenReturn(Optional.of(userEntity));
        when(parkingLotRepository.findByName(request.getParkingLotName())).thenReturn(Optional.of(parkingLotEntity));
        doNothing().when(userUnlinkFromParkingLotListener).handleUserUnlinkFromParkLotEvent(any(UserToParkingLotDto.class));
        when(daoMapper.map(userEntity)).thenReturn(user);

        ResponseDto result = parkingLotService.unlinkUserFromParkingLot(request);
        ResponseDto responseDto = new ResponseDto("John has been unlinked");

        verify(userRepository).findByCredential_Email(request.getUserEmail());
        verify(parkingLotRepository).findByName(request.getParkingLotName());

        assertFalse(userEntity.getParkingLots().contains(parkingLotEntity));
        assertNotNull(result);
        assertEquals(responseDto.getMessage(), result.getMessage());
    }

    @Test
    void unlinkUserFromParkingLotNotLinked_ShouldThrowEntityLinkException() {
        final UpdateParkLotLinkRequest request = new UpdateParkLotLinkRequest("user@example.com", "ParkingLotName");
        final RoleEntity roleEntity = new RoleEntity(1, "User");
        final CredentialsEntity credentialsEntity = new CredentialsEntity(1, null, "user@example.com", "Password");
        final UserEntity userEntity = new UserEntity(1, credentialsEntity, "John", "868521164", roleEntity, null, new HashSet<>());
        final ParkingLotEntity parkingLotEntity = new ParkingLotEntity(1, "ParkingLotName", "address", LocalTime.of(8, 0), LocalTime.of(20, 0), false, null);

        when(userRepository.findByCredential_Email(request.getUserEmail())).thenReturn(Optional.of(userEntity));
        when(parkingLotRepository.findByName(request.getParkingLotName())).thenReturn(Optional.of(parkingLotEntity));

        assertThrows(EntityLinkException.class, () -> parkingLotService.unlinkUserFromParkingLot(request));
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
        final ParkingSpotEntity parkingSpotEntity = new ParkingSpotEntity(1, null, "1spot", true, "regular", userEntity);
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
        final Role role = new Role(1, "Admin");
        final User user = User.builder()
                .id(1)
                .name("username")
                .phone("063251148")
                .role(role)
                .build();
        final WorkingTime workingTime = WorkingTime.builder()
                .nameDay(monday)
                .build();
        final ParkingLevel parkingLevel = ParkingLevel.builder()
                .id(1)
                .floor(1)
                .totalSpots(1)
                .build();
        final ParkingLot parkingLot = new ParkingLot(1, nameOfParkingLot, addressOfParkingLot, NOON, NOON, true, Set.of(user));

        // Dto
        final WorkingTimeDto workingTimeDto = new WorkingTimeDto("Monday");
        final ParkingLotDetailsDto parkingLotDetailsDto = new ParkingLotDetailsDto(parkingLot.getId(), nameOfParkingLot, addressOfParkingLot, NOON, NOON, Set.of(workingTimeDto), true, 1, 0);

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

    @Test
    public void testGetSpecificParkingLot_ShouldReturnParkingLot() {
        // Constants
        final String userEmail = "user@mail.com";
        final Integer parkingLotId = 1;
        final String name = "Name";
        final String address = "Address";
        final String day = "Monday";
        final boolean state = true;

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
        final ParkingLotEntity parkingLotEntity = new ParkingLotEntity(1, name, address, NOON, MIDNIGHT, state, Set.of(userEntity));
        final WorkingTimeEntity workingTimeEntity = new WorkingTimeEntity();
        workingTimeEntity.setId(1);
        workingTimeEntity.setNameDay(day);
        final ParkingLevelEntity parkingLevelEntity = ParkingLevelEntity.builder()
                .id(1)
                .floor(1)
                .totalSpots(1)
                .build();

        // Domain
        final WorkingTime workingTime = WorkingTime.builder()
                .nameDay(name)
                .build();
        final ParkingLevel parkingLevel = ParkingLevel.builder()
                .id(1)
                .floor(1)
                .totalSpots(1)
                .build();
        final User user = User.builder()
                .id(1)
                .name("username")
                .phone("068578869")
                .build();
        final ParkingLot parkingLot = new ParkingLot(1, name, address, NOON, MIDNIGHT, state, Set.of(user));

        // Dto
        final WorkingTimeDto workingTimeDto = WorkingTimeDto.builder()
                .nameDay(name)
                .build();
        final Set<ParkingSpotDtoAdmin> parkingSpots = new HashSet<>();
        final ParkingSpotDtoAdmin parkingSpotDtoAdmin = new ParkingSpotDtoAdmin(1, name, true, "regular", 1, "023154485");
        parkingSpots.add(parkingSpotDtoAdmin);
        final ParkingLevelDetailsDto parkingLevelDetailsDto = ParkingLevelDetailsDto.builder()
                .id(1)
                .floor(1)
                .totalSpots(1)
                .parkingSpots(parkingSpots)
                .build();
        final ParkingLotDto parkingLotDto = new ParkingLotDto(1, name, address, NOON, MIDNIGHT, state, Set.of(workingTimeDto), Set.of(parkingLevelDetailsDto));

        when(parkingLotRepository.findById(any())).thenReturn(Optional.of(parkingLotEntity));
        when(daoMapper.map(any(ParkingLotEntity.class))).thenReturn(parkingLot);
        when(dtoMapper.map(any(ParkingLot.class))).thenReturn(parkingLotDto);
        when(workingTimeRepository.findByParkingLot_Id(any())).thenReturn(Optional.of(Set.of(workingTimeEntity)));
        when(daoMapper.map(ArgumentMatchers.<Set<WorkingTimeEntity>>any())).thenReturn(Set.of(workingTime));
        when(dtoMapper.mapWorkingTimes(any())).thenReturn(Set.of(workingTimeDto));
        when(parkingLevelRepository.getByParkingLotId(any())).thenReturn(List.of(parkingLevelEntity));
        when(daoMapper.map(any(Set.class))).thenReturn(Set.of(parkingLevel));
        when(dtoMapper.map(any(Set.class))).thenReturn(Set.of(parkingLevelDetailsDto));
        when(userRepository.findByCredential_Email(any())).thenReturn(Optional.of(userEntity));
        when(userRepository.findUserEntityByParkingSpot_Id(any())).thenReturn(Optional.of(userEntity));

        final ParkingLotDto result = parkingLotService.getSpecificParkingLot(userEmail, parkingLotId);

        verify(parkingLotRepository).findById(any());
        verify(daoMapper).map(any(ParkingLotEntity.class));
        verify(dtoMapper).map(any(ParkingLot.class));
        verify(workingTimeRepository).findByParkingLot_Id(any());
        verify(daoMapper).map(ArgumentMatchers.<Set<WorkingTimeEntity>>any());
        verify(dtoMapper).mapWorkingTimes(any());
        verify(parkingLevelRepository).getByParkingLotId(any());
        verify(daoMapper).map(any(Set.class));
        verify(dtoMapper).map(any(Set.class));
        verify(userRepository).findByCredential_Email(any());
        verify(userRepository, times(1)).findUserEntityByParkingSpot_Id(any());

        assertThat(result).usingRecursiveComparison().isEqualTo(parkingLotDto);
    }

    @Test
    public void testGetSpecificParkingLot_WhenParkingLotNotExisting_ShouldThrowEntityNotFoundException() {

        final String userEmail = "user@mail.com";
        final Integer parkingLotId = 2;
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
        final ParkingLotEntity parkingLotEntity = new ParkingLotEntity(1, "name", "address", NOON, MIDNIGHT, true, Set.of(userEntity));

        when(parkingLotRepository.findById(parkingLotId)).thenReturn(empty());
        when(userRepository.findByCredential_Email(anyString())).thenReturn(Optional.of(userEntity));

        assertThrows(EntityNotFoundException.class, () -> parkingLotService.getSpecificParkingLot(userEmail, parkingLotId));

        verify(parkingLotRepository).findById(any());
        verify(userRepository).findByCredential_Email(anyString());
    }

    @Test
    void deleteParkingLotWithAllRelatedEntities_isSuccess() {
        Integer parkingLotId = 1;
        ParkingLotEntity parkingLotEntity = new ParkingLotEntity();
        List<ParkingLevelEntity> levels = List.of(new ParkingLevelEntity());
        List<ParkingSpotEntity> spots = List.of(new ParkingSpotEntity());
        Set<WorkingTimeEntity> workingTimes = new HashSet<>(List.of(new WorkingTimeEntity()));

        when(parkingLotRepository.findById(parkingLotId)).thenReturn(Optional.of(parkingLotEntity));
        when(parkingLevelRepository.getByParkingLotId(parkingLotEntity.getId())).thenReturn(levels);
        when(parkingSpotRepository.findByParkingLevelId(levels.get(0).getId())).thenReturn(Optional.of(spots));
        when(workingTimeRepository.findByParkingLot_Id(parkingLotEntity.getId())).thenReturn(Optional.of(workingTimes));

        ResponseDto response = parkingLotService.deleteParkingLot(parkingLotId);

        assertEquals("The parking lot with ID: " + parkingLotId + " and all its related entities has been deleted", response.getMessage());
        assertTrue(parkingLotRepository.findById(parkingLotEntity.getId()).isEmpty());
        assertTrue(levels.stream().allMatch(level -> parkingLevelRepository.findById(level.getId()).isEmpty()));
        assertTrue(spots.stream().allMatch(spot -> parkingSpotRepository.findById(spot.getId()).isEmpty()));
        assertTrue(workingTimes.stream().allMatch(time -> workingTimeRepository.findById(time.getId()).isEmpty()));
    }

    @Test
    void deleteParkingLotWithNoRelatedEntities_isSuccess() {
        Integer parkingLotId = 2;
        ParkingLotEntity parkingLotEntity = new ParkingLotEntity();

        when(parkingLotRepository.findById(parkingLotId)).thenReturn(Optional.of(parkingLotEntity));
        when(parkingLevelRepository.getByParkingLotId(parkingLotEntity.getId())).thenReturn(emptyList());
        when(workingTimeRepository.findByParkingLot_Id(parkingLotEntity.getId())).thenReturn(Optional.empty());

        ResponseDto response = parkingLotService.deleteParkingLot(parkingLotId);

        assertEquals("The parking lot with ID: " + parkingLotId + " and all its related entities has been deleted", response.getMessage());
        verify(parkingLotRepository).delete(parkingLotEntity);
        verify(parkingLevelRepository, never()).deleteAll(any());
        verify(parkingSpotRepository, never()).deleteAll(any());
        verify(workingTimeRepository, never()).deleteAll(any());
    }

    @Test
    void deleteParkingLotNotFound_throwsEntityNotFoundException() {
        Integer parkingLotId = 3;
        when(parkingLotRepository.findById(parkingLotId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                parkingLotService.deleteParkingLot(parkingLotId));
    }

    @Test
    void testUpdateParkingLot_WhenNameExists_ShouldThrowEntityExistsException() {
        ParkingLotEntity existingParkingLot = new ParkingLotEntity();
        existingParkingLot.setId(1);
        existingParkingLot.setName("ExistingName");

        ParkingLotEntity updatingParkingLot = new ParkingLotEntity();
        updatingParkingLot.setId(2);
        updatingParkingLot.setName("UpdatingName");

        Set<WorkingTimeDto> workingTimeDto = Set.of(new WorkingTimeDto("Monday"));
        Set<ParkingLevelDto> parkingLevelDto = Set.of(new ParkingLevelDto(1, 1, 100));
        ParkingLotRequest parkingLotRequest = new ParkingLotRequest("ExistingName", "NewAddress", MIDNIGHT, NOON, true, workingTimeDto, parkingLevelDto);

        when(parkingLotRepository.getParkingLotEntitiesById(2)).thenReturn(Optional.of(updatingParkingLot));
        when(parkingLotRepository.existsByName("ExistingName")).thenReturn(true);

        assertThrows(EntityExistsException.class, () ->
                parkingLotService.updateParkingLot(2, parkingLotRequest));

        verify(parkingLotRepository).getParkingLotEntitiesById(2);
        verify(parkingLotRepository).existsByName("ExistingName");
    }

    @Test
    void testUpdateParkingLot_WhenAddressExists_ShouldThrowEntityExistsException() {
        ParkingLotEntity existingParkingLot = new ParkingLotEntity();
        existingParkingLot.setId(1);
        existingParkingLot.setName("ExistingName");
        existingParkingLot.setAddress("ExistingAddress");

        ParkingLotEntity updatingParkingLot = new ParkingLotEntity();
        updatingParkingLot.setId(2);
        updatingParkingLot.setName("UpdatingName");
        updatingParkingLot.setAddress("UpdatingAddress");

        Set<WorkingTimeDto> workingTimeDto = Set.of(new WorkingTimeDto("Monday"));
        Set<ParkingLevelDto> parkingLevelDto = Set.of(new ParkingLevelDto(1, 1, 100));
        ParkingLotRequest parkingLotRequest = new ParkingLotRequest("UpdatingName", "ExistingAddress", MIDNIGHT, NOON, true, workingTimeDto, parkingLevelDto);


        when(parkingLotRepository.getParkingLotEntitiesById(2)).thenReturn(java.util.Optional.of(updatingParkingLot));
        when(parkingLotRepository.existsByAddress("ExistingAddress")).thenReturn(true);

        assertThrows(EntityExistsException.class, () ->
                parkingLotService.updateParkingLot(2, parkingLotRequest));

        verify(parkingLotRepository).getParkingLotEntitiesById(2);
        verify(parkingLotRepository).existsByAddress("ExistingAddress");
    }

    @Test
    void testUpdateParkingLot_WhenParkingLotNotFound_ShouldThrowEntityNotFoundException() {
        when(parkingLotRepository.getParkingLotEntitiesById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                parkingLotService.updateParkingLot(1, any(ParkingLotRequest.class)));

        verify(parkingLotRepository).getParkingLotEntitiesById(1);
    }

    @Test
    void testUpdateParkingLot_WhenStartHourIsAfterEndHour_ShouldThrowDateTimeException() {
        ParkingLotEntity existingParkingLot = new ParkingLotEntity();
        existingParkingLot.setId(1);
        existingParkingLot.setName("ExistingName");
        existingParkingLot.setAddress("ExistingAddress");

        Set<WorkingTimeDto> workingTimeDto = Set.of(new WorkingTimeDto("Monday"));
        Set<ParkingLevelDto> parkingLevelDto = Set.of(new ParkingLevelDto(1, 1, 100));
        ParkingLotRequest parkingLotRequest = new ParkingLotRequest("UpdatingName", "ExistingAddress", LocalTime.of(20, 0), LocalTime.of(10, 0), true, workingTimeDto, parkingLevelDto);

        when(parkingLotRepository.getParkingLotEntitiesById(1)).thenReturn(Optional.of(existingParkingLot));

        assertThrows(DateTimeException.class, () ->
                parkingLotService.updateParkingLot(1, parkingLotRequest));

        verify(parkingLotRepository).getParkingLotEntitiesById(1);
    }

    @Test
    void testUpdateParkingLot_WhenGivenWrongWeekDay_ShouldThrowDateTimeException() {
        ParkingLotEntity existingParkingLot = new ParkingLotEntity();
        existingParkingLot.setId(1);
        existingParkingLot.setName("ExistingName");
        existingParkingLot.setAddress("ExistingAddress");

        Set<WorkingTimeDto> workingTimeDto = Set.of(new WorkingTimeDto("Actually not even close to a day of the week"));
        Set<ParkingLevelDto> parkingLevelDto = Set.of(new ParkingLevelDto(1, 1, 100));
        ParkingLotRequest parkingLotRequest = new ParkingLotRequest("UpdatingName", "ExistingAddress", MIDNIGHT, NOON, true, workingTimeDto, parkingLevelDto);

        when(parkingLotRepository.getParkingLotEntitiesById(1)).thenReturn(Optional.of(existingParkingLot));

        assertThrows(DateTimeException.class, () ->
                parkingLotService.updateParkingLot(1, parkingLotRequest));

        verify(parkingLotRepository).getParkingLotEntitiesById(1);
    }

    @Test
    void testUpdateParkingLot_WhenGivenTwoOfTheSameWeekDay_ShouldThrowDateTimeException() {
        ParkingLotEntity existingParkingLot = new ParkingLotEntity();
        existingParkingLot.setId(1);
        existingParkingLot.setName("ExistingName");
        existingParkingLot.setAddress("ExistingAddress");

        WorkingTimeDto monday1 = new WorkingTimeDto("Monday");
        WorkingTimeDto monday2 = new WorkingTimeDto("Monday");

        Set<WorkingTimeDto> workingTimeDto = Set.of(monday1, monday2);
        Set<ParkingLevelDto> parkingLevelDto = Set.of(new ParkingLevelDto(1, 1, 100));
        ParkingLotRequest parkingLotRequest = new ParkingLotRequest("UpdatingName", "ExistingAddress", MIDNIGHT, NOON, true, workingTimeDto, parkingLevelDto);

        when(parkingLotRepository.getParkingLotEntitiesById(1)).thenReturn(Optional.of(existingParkingLot));

        assertThrows(DateTimeException.class, () ->
                parkingLotService.updateParkingLot(1, parkingLotRequest));

        verify(parkingLotRepository).getParkingLotEntitiesById(1);
    }

    @Test
    void testUpdateParkingLot_WhenGivenMoreThanFiveFloors_ShouldInvalidRequestParameterException() {
        ParkingLotEntity existingParkingLot = new ParkingLotEntity();
        existingParkingLot.setId(1);
        existingParkingLot.setName("ExistingName");
        existingParkingLot.setAddress("ExistingAddress");

        Set<WorkingTimeDto> workingTimeDto = Set.of(new WorkingTimeDto("Monday"));
        Set<ParkingLevelDto> parkingLevelDto = Set.of(new ParkingLevelDto(1, 1, 100),
                new ParkingLevelDto(2, 2, 100),
                new ParkingLevelDto(3, 3, 100),
                new ParkingLevelDto(4, 4, 100),
                new ParkingLevelDto(5, 5, 100),
                new ParkingLevelDto(6, 6, 100));
        ParkingLotRequest parkingLotRequest = new ParkingLotRequest("UpdatingName", "ExistingAddress", MIDNIGHT, NOON, true, workingTimeDto, parkingLevelDto);

        when(parkingLotRepository.getParkingLotEntitiesById(1)).thenReturn(Optional.of(existingParkingLot));

        assertThrows(InvalidRequestParameterException.class, () ->
                parkingLotService.updateParkingLot(1, parkingLotRequest));

        verify(parkingLotRepository).getParkingLotEntitiesById(1);
    }

    @Test
    void testUpdateParkingLot_WhenGivingMoreSpots_ShouldReturnUpdatedMessage() {
        ParkingLotEntity existingParkingLot = new ParkingLotEntity();
        existingParkingLot.setId(1);
        existingParkingLot.setName("ExistingName");
        existingParkingLot.setAddress("ExistingAddress");

        ParkingLevelEntity parkingLevelEntity = ParkingLevelEntity.builder()
                .id(1)
                .parkingLot(existingParkingLot)
                .floor(1)
                .totalSpots(1)
                .build();

        ParkingSpotEntity parkingSpotEntity = new ParkingSpotEntity(1, parkingLevelEntity, "A-001", true, "Regular", null);

        parkingLevelEntity.setParkingSpots(Set.of(parkingSpotEntity));

        Set<WorkingTimeDto> workingTimeDto = Set.of(new WorkingTimeDto("Monday"));
        Set<ParkingLevelDto> parkingLevelDto = Set.of(new ParkingLevelDto(1, 1, 2));
        ParkingLotRequest parkingLotRequest = new ParkingLotRequest("ExistingAddress", "ExistingAddress", MIDNIGHT, NOON, true, workingTimeDto, parkingLevelDto);

        when(parkingLotRepository.getParkingLotEntitiesById(1)).thenReturn(Optional.of(existingParkingLot));
        when(parkingLevelRepository.getByParkingLotId(1)).thenReturn(List.of(parkingLevelEntity));

        parkingLotService.updateParkingLot(1, parkingLotRequest);

        verify(parkingLotRepository).getParkingLotEntitiesById(1);
        verify(parkingLevelRepository, times(2)).getByParkingLotId(1);
    }

    @Test
    void testUpdateParkingLot_WhenGivingMoreFloors_ShouldReturnUpdatedMessage() {
        ParkingLotEntity existingParkingLot = new ParkingLotEntity();
        existingParkingLot.setId(1);
        existingParkingLot.setName("ExistingName");
        existingParkingLot.setAddress("ExistingAddress");

        ParkingLevelEntity parkingLevelEntity = ParkingLevelEntity.builder()
                .id(1)
                .parkingLot(existingParkingLot)
                .floor(1)
                .totalSpots(1)
                .build();

        ParkingSpotEntity parkingSpotEntity = new ParkingSpotEntity(1, parkingLevelEntity, "A-001", true, "Regular", null);

        parkingLevelEntity.setParkingSpots(Set.of(parkingSpotEntity));

        Set<WorkingTimeDto> workingTimeDto = Set.of(new WorkingTimeDto("Monday"));
        Set<ParkingLevelDto> parkingLevelDto = Set.of(new ParkingLevelDto(1, 1, 1), new ParkingLevelDto(2, 2, 1));
        ParkingLotRequest parkingLotRequest = new ParkingLotRequest("ExistingAddress", "ExistingAddress", MIDNIGHT, NOON, true, workingTimeDto, parkingLevelDto);

        when(parkingLotRepository.getParkingLotEntitiesById(1)).thenReturn(Optional.of(existingParkingLot));
        when(parkingLevelRepository.getByParkingLotId(1)).thenReturn(List.of(parkingLevelEntity));

        parkingLotService.updateParkingLot(1, parkingLotRequest);

        verify(parkingLotRepository).getParkingLotEntitiesById(1);
        verify(parkingLevelRepository, times(2)).getByParkingLotId(1);
    }

    @Test
    void testUpdateParkingLot_WhenGivingLessSpots_ShouldReturnUpdatedMessage() {
        ParkingLotEntity existingParkingLot = new ParkingLotEntity();
        existingParkingLot.setId(1);
        existingParkingLot.setName("ExistingName");
        existingParkingLot.setAddress("ExistingAddress");

        ParkingLevelEntity parkingLevelEntity = ParkingLevelEntity.builder()
                .id(1)
                .parkingLot(existingParkingLot)
                .floor(1)
                .totalSpots(2)
                .build();

        ParkingSpotEntity parkingSpotEntity = new ParkingSpotEntity(1, parkingLevelEntity, "A-001", true, "Regular", null);
        ParkingSpotEntity parkingSpotEntity2 = new ParkingSpotEntity(2, parkingLevelEntity, "A-002", true, "Regular", null);

        parkingLevelEntity.setParkingSpots(Set.of(parkingSpotEntity, parkingSpotEntity2));

        Set<WorkingTimeDto> workingTimeDto = Set.of(new WorkingTimeDto("Monday"));
        Set<ParkingLevelDto> parkingLevelDto = Set.of(new ParkingLevelDto(1, 1, 1));
        ParkingLotRequest parkingLotRequest = new ParkingLotRequest("ExistingAddress", "ExistingAddress", MIDNIGHT, NOON, true, workingTimeDto, parkingLevelDto);

        when(parkingLotRepository.getParkingLotEntitiesById(1)).thenReturn(Optional.of(existingParkingLot));
        when(parkingLevelRepository.getByParkingLotId(1)).thenReturn(List.of(parkingLevelEntity));
        when(parkingSpotRepository.getAllParkingSpotEntitiesByParkingLevelIdOrderByIdDesc(1)).thenReturn(List.of(parkingSpotEntity, parkingSpotEntity2));

        parkingLotService.updateParkingLot(1, parkingLotRequest);

        verify(parkingLotRepository).getParkingLotEntitiesById(1);
        verify(parkingLevelRepository, times(2)).getByParkingLotId(1);
        verify(parkingSpotRepository).getAllParkingSpotEntitiesByParkingLevelIdOrderByIdDesc(1);

    }

    @Test
    void testUpdateParkingLot_WhenGivingLessFloors_ShouldReturnUpdatedMessage() {
        ParkingLotEntity existingParkingLot = new ParkingLotEntity();
        existingParkingLot.setId(1);
        existingParkingLot.setName("ExistingName");
        existingParkingLot.setAddress("ExistingAddress");

        ParkingLevelEntity parkingLevelEntity = ParkingLevelEntity.builder()
                .id(1)
                .parkingLot(existingParkingLot)
                .floor(1)
                .totalSpots(1)
                .build();

        ParkingSpotEntity parkingSpotEntity = new ParkingSpotEntity(1, parkingLevelEntity, "A-001", true, "Regular", null);

        parkingLevelEntity.setParkingSpots(Set.of(parkingSpotEntity));

        ParkingLevelEntity parkingLevelEntity1 = ParkingLevelEntity.builder()
                .id(2)
                .parkingLot(existingParkingLot)
                .floor(2)
                .totalSpots(1)
                .build();

        ParkingSpotEntity parkingSpotEntity1 = new ParkingSpotEntity(2, parkingLevelEntity1, "B-001", true, "Regular", null);

        parkingLevelEntity1.setParkingSpots(Set.of(parkingSpotEntity1));

        Set<WorkingTimeDto> workingTimeDto = Set.of(new WorkingTimeDto("Monday"));
        Set<ParkingLevelDto> parkingLevelDto = Set.of(new ParkingLevelDto(1, 1, 1));
        ParkingLotRequest parkingLotRequest = new ParkingLotRequest("ExistingAddress", "ExistingAddress", MIDNIGHT, NOON, true, workingTimeDto, parkingLevelDto);

        when(parkingLotRepository.getParkingLotEntitiesById(1)).thenReturn(Optional.of(existingParkingLot));
        when(parkingLevelRepository.getByParkingLotId(1)).thenReturn(List.of(parkingLevelEntity, parkingLevelEntity1)).thenReturn(List.of(parkingLevelEntity));
        when(parkingSpotRepository.findByParkingLevelId(2)).thenReturn(Optional.of(List.of(parkingSpotEntity1)));

        parkingLotService.updateParkingLot(1, parkingLotRequest);

        verify(parkingLotRepository).getParkingLotEntitiesById(1);
        verify(parkingLevelRepository, times(2)).getByParkingLotId(1);
        verify(parkingSpotRepository).findByParkingLevelId(2);
    }
}