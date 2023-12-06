package com.endava.internship.infrastructure.service;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.endava.internship.dao.entity.CredentialsEntity;
import com.endava.internship.dao.entity.ParkingLevelEntity;
import com.endava.internship.dao.entity.ParkingSpotEntity;
import com.endava.internship.dao.entity.RoleEntity;
import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.dao.repository.ParkingSpotRepository;
import com.endava.internship.dao.repository.UserRepository;
import com.endava.internship.infrastructure.domain.ParkingLevel;
import com.endava.internship.infrastructure.domain.ParkingSpot;
import com.endava.internship.infrastructure.domain.Role;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.infrastructure.exception.InvalidRequestParameterException;
import com.endava.internship.infrastructure.mapper.DaoMapper;
import com.endava.internship.infrastructure.security.UserDetailsImpl;
import com.endava.internship.web.dto.ParkingSpotDto;
import com.endava.internship.web.dto.ResponseDto;
import com.endava.internship.web.request.SpotOccupancyRequest;
import com.endava.internship.web.request.UpdateParkingSpotRequest;

import jakarta.persistence.EntityNotFoundException;

import static com.endava.internship.infrastructure.utils.TestUtils.SPOT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    private DaoMapper daoMapper;
    @Mock
    private ParkingSpotRepository parkingSpotRepository;
    @InjectMocks
    private ParkingSpotServiceImpl parkingSpotService;

    @Test
    public void whenEditParkingSpotCalled_WithValidSpotIdAndUpdateRequest_ShouldReturnUpdatedDto() {
        final UpdateParkingSpotRequest spotToUpdate = new UpdateParkingSpotRequest("New Type");

        final ParkingSpotEntity existingSpot = new ParkingSpotEntity();
        existingSpot.setId(SPOT_ID);
        existingSpot.setType("Old Type");
        existingSpot.setAvailable(true);

        final ParkingSpotEntity updatedSpot = new ParkingSpotEntity();
        updatedSpot.setId(SPOT_ID);
        updatedSpot.setType(spotToUpdate.getType());

        final ParkingSpot parkingSpot = new ParkingSpot(1, null, existingSpot.isAvailable(), "New Type", null, null);

        when(parkingSpotRepository.findById(SPOT_ID)).thenReturn(Optional.of(existingSpot));
        when(daoMapper.map(any(ParkingSpotEntity.class))).thenReturn(parkingSpot);
        when(parkingSpotRepository.save(any(ParkingSpotEntity.class))).thenReturn(updatedSpot);

        final ResponseDto result = parkingSpotService.editParkingSpot(SPOT_ID, spotToUpdate);

        assertEquals(spotToUpdate.getType(), result.getMessage());
        verify(parkingSpotRepository).findById(SPOT_ID);
        verify(parkingSpotRepository).save(any(ParkingSpotEntity.class));
    }

    @Test
    public void whenEditParkingSpotCalled_WithUnavailableParkingSpot_ShouldThrowInvalidRequestParameterException() {
        final ParkingSpotEntity existingSpot = new ParkingSpotEntity();
        existingSpot.setId(SPOT_ID);
        existingSpot.setAvailable(false);

        final ParkingSpot parkingSpot = new ParkingSpot(1, null, false, "New type", null, null);

        when(daoMapper.map(existingSpot)).thenReturn(parkingSpot);
        when(parkingSpotRepository.findById(SPOT_ID)).thenReturn(Optional.of(existingSpot));

        assertThrows(InvalidRequestParameterException.class, () ->
                parkingSpotService.editParkingSpot(SPOT_ID, any(UpdateParkingSpotRequest.class))
        );

        verify(parkingSpotRepository).findById(SPOT_ID);
        verify(parkingSpotRepository, times(0)).save(any(ParkingSpotEntity.class));
    }

    @Test
    void occupyParkingSpot_WhenUserEmailsDoNotMatch_ShouldThrowIllegalArgumentException() {
        Integer spotId = 1;
        SpotOccupancyRequest request = new SpotOccupancyRequest("test@example.com");
        final CredentialsEntity credentialsEntity = CredentialsEntity.builder()
                .id(1)
                .email("different@example.com")
                .password("password")
                .build();
        final RoleEntity roleEntity = new RoleEntity(2, "User");
        final UserEntity userEntity = UserEntity.builder()
                .id(1)
                .credential(credentialsEntity)
                .name("username")
                .phone("063251148")
                .role(roleEntity)
                .build();
        UserDetails userDetails = new UserDetailsImpl(userEntity);

        assertThrows(IllegalArgumentException.class, () ->
                parkingSpotService.occupyParkingSpot(spotId, request, userDetails));
    }

    @Test
    void whenOccupyingNonexistentParkingSpot_ShouldThrowEntityNotFoundException() {
        Integer spotId = 1;
        SpotOccupancyRequest request = new SpotOccupancyRequest("test@example.com");
        final CredentialsEntity credentialsEntity = CredentialsEntity.builder()
                .id(1)
                .email("test@example.com")
                .password("password")
                .build();
        final RoleEntity roleEntity = new RoleEntity(2, "User");
        final UserEntity userEntity = UserEntity.builder()
                .id(1)
                .credential(credentialsEntity)
                .name("username")
                .phone("063251148")
                .role(roleEntity)
                .build();
        UserDetails userDetails = new UserDetailsImpl(userEntity);

        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                parkingSpotService.occupyParkingSpot(spotId, request, userDetails));
    }

    @Test
    void whenOccupyingSpotForNonexistentUser_ShouldThrowEntityNotFoundException() {
        Integer spotId = 1;
        SpotOccupancyRequest request = new SpotOccupancyRequest("test@example.com");
        String email = request.getUserEmail();
        final CredentialsEntity credentialsEntity = CredentialsEntity.builder()
                .id(1)
                .email("test@example.com")
                .password("password")
                .build();
        final RoleEntity roleEntity = new RoleEntity(2, "User");
        final UserEntity userEntity = UserEntity.builder()
                .id(1)
                .credential(credentialsEntity)
                .name("username")
                .phone("063251148")
                .role(roleEntity)
                .build();
        UserDetails userDetails = new UserDetailsImpl(userEntity);
        final ParkingLevelEntity parkingLevelEntity = ParkingLevelEntity.builder()
                .id(1)
                .totalSpots(10)
                .floor(5)
                .build();
        final ParkingSpotEntity parkingSpotEntity = new ParkingSpotEntity();
        parkingSpotEntity.setId(spotId);
        parkingSpotEntity.setParkingLevel(parkingLevelEntity);
        parkingSpotEntity.setName("A-001");
        parkingSpotEntity.setAvailable(true);
        final ParkingLevel parkingLevel = ParkingLevel.builder()
                .id(1)
                .totalSpots(10)
                .floor(5)
                .build();
        final ParkingSpot parkingSpot = ParkingSpot.builder()
                .id(1)
                .parkingLevel(parkingLevel)
                .name("A-001")
                .available(true)
                .build();

        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.of(parkingSpotEntity));
        when(daoMapper.map(any(ParkingSpotEntity.class))).thenReturn(parkingSpot);
        when(daoMapper.map(any(ParkingLevelEntity.class))).thenReturn(parkingLevel);
        when(userRepository.findByCredential_Email(email)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                parkingSpotService.occupyParkingSpot(spotId, request, userDetails));
    }

    @Test
    void whenOccupyingAlreadyOccupiedParkingSpot_ShouldThrowIllegalStateException() {
        Integer spotId = 1;
        SpotOccupancyRequest request = new SpotOccupancyRequest("test@example.com");
        final CredentialsEntity credentialsEntity = CredentialsEntity.builder()
                .id(1)
                .email("test@example.com")
                .password("password")
                .build();
        final RoleEntity roleEntity = new RoleEntity(2, "User");
        final UserEntity userEntity = UserEntity.builder()
                .id(1)
                .credential(credentialsEntity)
                .name("username")
                .phone("063251148")
                .role(roleEntity)
                .build();
        UserDetails userDetails = new UserDetailsImpl(userEntity);
        ParkingSpotEntity parkingSpotEntity = new ParkingSpotEntity();
        parkingSpotEntity.setId(spotId);
        parkingSpotEntity.setName("A-001");
        parkingSpotEntity.setAvailable(false);

        ParkingSpot parkingSpot = ParkingSpot.builder()
                .id(1)
                .name("A-001")
                .available(false)
                .build();

        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.of(parkingSpotEntity));
        when(daoMapper.map(any(ParkingSpotEntity.class))).thenReturn(parkingSpot);

        assertThrows(IllegalStateException.class, () ->
                parkingSpotService.occupyParkingSpot(spotId, request, userDetails));
    }

    @Test
    public void whenOccupyingAvailableParkingSpot_ShouldReturnParkingSpotDto() {
        final Integer spotId = 1;
        SpotOccupancyRequest request = new SpotOccupancyRequest("test@example.com");
        String email = request.getUserEmail();
        final CredentialsEntity credentialsEntity = CredentialsEntity.builder()
                .id(1)
                .email("test@example.com")
                .password("password")
                .build();
        final RoleEntity roleEntity = new RoleEntity(2, "User");
        final UserEntity userEntity = UserEntity.builder()
                .id(1)
                .credential(credentialsEntity)
                .name("username")
                .phone("063251148")
                .role(roleEntity)
                .build();
        UserDetails userDetails = new UserDetailsImpl(userEntity);
        final ParkingLevelEntity parkingLevelEntity = ParkingLevelEntity.builder()
                .id(1)
                .totalSpots(10)
                .floor(5)
                .build();
        final ParkingSpotEntity parkingSpotEntity = new ParkingSpotEntity();
        parkingSpotEntity.setId(spotId);
        parkingSpotEntity.setParkingLevel(parkingLevelEntity);
        parkingSpotEntity.setName("A-001");
        parkingSpotEntity.setAvailable(true);
        final ParkingLevel parkingLevel = ParkingLevel.builder()
                .id(1)
                .totalSpots(10)
                .floor(5)
                .build();
        final ParkingSpot parkingSpot = ParkingSpot.builder()
                .id(1)
                .parkingLevel(parkingLevel)
                .name("A-001")
                .available(true)
                .build();
        final Role role = new Role(2, "User");
        final User user = User.builder()
                .id(1)
                .name("username")
                .phone("063251148")
                .role(role)
                .build();

        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.of(parkingSpotEntity));
        when(daoMapper.map(any(ParkingSpotEntity.class))).thenReturn(parkingSpot);
        when(daoMapper.map(any(ParkingLevelEntity.class))).thenReturn(parkingLevel);
        when(userRepository.findByCredential_Email(email)).thenReturn(Optional.of(userEntity));
        when(daoMapper.map(any(UserEntity.class))).thenReturn(user);
        when(daoMapper.map(any(ParkingSpot.class))).thenReturn(parkingSpotEntity);
        when(parkingSpotRepository.save(any(ParkingSpotEntity.class))).thenReturn(parkingSpotEntity);
        when(daoMapper.map(any(User.class))).thenReturn(userEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        ParkingSpotDto responseDto = parkingSpotService.occupyParkingSpot(spotId, request, userDetails);

        assertEquals("A-001", responseDto.getName());

        verify(parkingSpotRepository).findById(spotId);
        verify(daoMapper, times(2)).map(any(ParkingSpotEntity.class));
        verify(daoMapper).map(any(ParkingLevelEntity.class));
        verify(userRepository).findByCredential_Email(email);
        verify(daoMapper).map(any(UserEntity.class));
        verify(daoMapper).map(any(ParkingSpot.class));
        verify(parkingSpotRepository).save(any(ParkingSpotEntity.class));
        verify(daoMapper).map(any(User.class));
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    public void whenUserAlreadyLinkedToParkingSpot_ShouldThrowIllegalStateException() {
        final Integer spotId = 1;
        SpotOccupancyRequest request = new SpotOccupancyRequest("test@example.com");
        String email = request.getUserEmail();
        final CredentialsEntity credentialsEntity = CredentialsEntity.builder()
                .id(1)
                .email("test@example.com")
                .password("password")
                .build();
        final RoleEntity roleEntity = new RoleEntity(2, "User");
        final UserEntity userEntity = UserEntity.builder()
                .id(1)
                .credential(credentialsEntity)
                .name("username")
                .phone("063251148")
                .role(roleEntity)
                .build();
        UserDetails userDetails = new UserDetailsImpl(userEntity);
        final ParkingLevelEntity parkingLevelEntity = ParkingLevelEntity.builder()
                .id(1)
                .totalSpots(10)
                .floor(5)
                .build();
        final ParkingSpotEntity parkingSpotEntity = new ParkingSpotEntity();
        parkingSpotEntity.setId(spotId);
        parkingSpotEntity.setParkingLevel(parkingLevelEntity);
        parkingSpotEntity.setName("A-001");
        parkingSpotEntity.setAvailable(true);
        final ParkingLevel parkingLevel = ParkingLevel.builder()
                .id(1)
                .totalSpots(10)
                .floor(5)
                .build();
        final ParkingSpot parkingSpot = ParkingSpot.builder()
                .id(1)
                .parkingLevel(parkingLevel)
                .name("A-001")
                .available(true)
                .build();
        final Role role = new Role(2, "User");
        final User user = User.builder()
                .id(1)
                .name("username")
                .phone("063251148")
                .role(role)
                .parkingSpot(parkingSpot)
                .build();

        when(parkingSpotRepository.findById(spotId)).thenReturn(Optional.of(parkingSpotEntity));
        when(daoMapper.map(any(ParkingSpotEntity.class))).thenReturn(parkingSpot);
        when(daoMapper.map(any(ParkingLevelEntity.class))).thenReturn(parkingLevel);
        when(userRepository.findByCredential_Email(email)).thenReturn(Optional.of(userEntity));
        when(daoMapper.map(any(UserEntity.class))).thenReturn(user);

        assertThrows(IllegalStateException.class, () ->
                parkingSpotService.occupyParkingSpot(spotId, request, userDetails));
    }
}