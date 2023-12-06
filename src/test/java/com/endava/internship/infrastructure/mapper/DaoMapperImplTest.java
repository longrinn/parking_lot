package com.endava.internship.infrastructure.mapper;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
class DaoMapperImplTest {

    private final DaoMapper daoMapper = Mappers.getMapper(DaoMapper.class);

    @Test
    void testRoleMap_WhenRoleEntityIsNull_ShouldReturnNullRole() {
        Role role = daoMapper.map((RoleEntity) null);

        assertNull(role);
    }

    @Test
    void testCredentialsEntityMap_WhenCredentialsIsValid_ShouldReturnCredentialsEntity() {
        Credentials credentials = new Credentials("user@mail.com", "User1!");

        CredentialsEntity credentialsEntity = daoMapper.map(credentials);

        assertEquals(credentials.getEmail(), credentialsEntity.getEmail());
        assertEquals(credentials.getPassword(), credentialsEntity.getPassword());
    }

    @Test
    void testCredentialsEntityMap_WhenCredentialsIsNull_ShouldReturnNullCredentialsEntity() {
        CredentialsEntity credentialsEntity = daoMapper.map((Credentials) null);

        assertNull(credentialsEntity);
    }

    @Test
    void testUserEntityMap_WhenUserIsValid_ShouldReturnValidUserEntity() {
        User user = User.builder()
                .id(1)
                .name("User")
                .phone("123456789")
                .role(new Role("User"))
                .build();

        UserEntity userEntity = daoMapper.map(user);

        assertEquals(user.getId(), userEntity.getId());
        assertEquals(user.getName(), userEntity.getName());
        assertEquals(user.getRole().getName(), userEntity.getRole().getName());
        assertEquals(user.getPhone(), userEntity.getPhone());
    }

    @Test
    void testUserEntityMap_WhenUserIsNull_ShouldReturnNullUserEntity() {
        UserEntity userEntity = daoMapper.map((User) null);

        assertNull(userEntity);
    }

    @Test
    void testUserMap_WhenUserEntityIsValid_ShouldReturnValidUser() {
        ParkingSpotEntity parkingSpotEntity = new ParkingSpotEntity(1, null, "A-001", false, "regular", null);
        UserEntity userEntity = UserEntity.builder()
                .id(5)
                .name("Admin")
                .phone("987654321")
                .role(new RoleEntity(2, "Admin"))
                .parkingSpot(parkingSpotEntity)
                .build();

        User user = daoMapper.map(userEntity);

        assertEquals(userEntity.getId(), user.getId());
        assertEquals(userEntity.getName(), user.getName());
        assertEquals(userEntity.getPhone(), user.getPhone());
        assertEquals(userEntity.getRole().getName(), user.getRole().getName());
        assertEquals(userEntity.getParkingSpot().getType(), user.getParkingSpot().getType());
    }

    @Test
    void testUserMap_WhenUserEntityIsValid_WithoutParkingSpotEntity_ShouldReturnValidUser() {
        UserEntity userEntity = UserEntity.builder()
                .id(5)
                .name("Admin")
                .phone("987654321")
                .role(new RoleEntity(2, "Admin"))
                .build();

        User user = daoMapper.map(userEntity);

        assertEquals(userEntity.getId(), user.getId());
        assertEquals(userEntity.getName(), user.getName());
        assertEquals(userEntity.getPhone(), user.getPhone());
        assertEquals(userEntity.getRole().getName(), user.getRole().getName());
    }

    @Test
    void testUserMap_WhenUserEntityIsNull_ShouldReturnNullUser() {
        User user = daoMapper.map((UserEntity) null);

        assertNull(user);
    }

    @Test
    void testRoleEntityMap_WhenRoleIsNull_ShouldReturnNullRoleEntity() {
        RoleEntity roleEntity = daoMapper.map((Role) null);

        assertNull(roleEntity);
    }

    @Test
    void testParkingLotEntityMap_WhenParkingLotIsValid_ShouldReturnParkingLotEntity() {
        User user = User.builder()
                .id(1)
                .name("User")
                .phone("123456789")
                .role(new Role("User"))
                .build();
        ParkingLot parkingLot = ParkingLot.builder()
                .id(1)
                .name("Parking Lot from test")
                .address("Random Street")
                .startTime(LocalTime.of(6, 0, 0))
                .endTime(LocalTime.of(21, 0, 0))
                .state(true)
                .users(Set.of(user))
                .build();

        ParkingLotEntity parkingLotEntity = daoMapper.map(parkingLot);

        assertEquals(parkingLot.getName(), parkingLotEntity.getName());
        assertEquals(parkingLot.getAddress(), parkingLotEntity.getAddress());
        assertEquals(parkingLot.getStartTime(), parkingLotEntity.getStartTime());
        assertEquals(parkingLot.getEndTime(), parkingLotEntity.getEndTime());
        assertEquals(parkingLot.isState(), parkingLotEntity.getState());
    }

    @Test
    void testParkingLotEntityMap_WhenParkingLotIsNull_ShouldReturnNullParkingLotEntity() {
        ParkingLotEntity parkingLotEntity = daoMapper.map((ParkingLot) null);

        assertNull(parkingLotEntity);
    }

    @Test
    void testParkingLevelEntityMap_WhenParkingLevelIsValid_ShouldReturnParkingLevelEntity() {
        ParkingSpot parkingSpot = ParkingSpot.builder()
                .available(true)
                .type("regular")
                .user(null)
                .build();

        ParkingLevel parkingLevel = ParkingLevel.builder()
                .id(1)
                .floor(1)
                .totalSpots(1)
                .parkingSpots(Set.of(parkingSpot))
                .build();

        ParkingLevelEntity parkingLevelEntity = daoMapper.map(parkingLevel);

        assertEquals(parkingLevel.getFloor(), parkingLevelEntity.getFloor());
        assertEquals(parkingLevel.getTotalSpots(), parkingLevelEntity.getTotalSpots());
    }

    @Test
    void testParkingLevelEntityMap_WhenParkingLevelHasNoUser_ShouldReturnParkingLevelEntity() {
        ParkingLevel parkingLevel = ParkingLevel.builder()
                .id(1)
                .floor(1)
                .totalSpots(1)
                .build();

        ParkingLevelEntity parkingLevelEntity = daoMapper.map(parkingLevel);

        assertEquals(parkingLevel.getFloor(), parkingLevelEntity.getFloor());
        assertEquals(parkingLevel.getTotalSpots(), parkingLevelEntity.getTotalSpots());
    }

    @Test
    void testParkingLevelEntityMap_WhenParkingLevelIsNull_ShouldReturnNullParkingLevelEntity() {
        ParkingLevelEntity parkingLevelEntity = daoMapper.map((ParkingLevel) null);

        assertNull(parkingLevelEntity);
    }

    @Test
    void testParkingSpotEntityMap_WhenParkingSpotIsValid_ShouldReturnParkingSpotEntity() {
        ParkingSpot parkingSpot = ParkingSpot.builder()
                .available(true)
                .type("regular")
                .user(null)
                .build();

        ParkingSpotEntity parkingSpotEntity = daoMapper.map(parkingSpot);

        assertEquals(parkingSpot.isAvailable(), parkingSpotEntity.isAvailable());
        assertEquals(parkingSpot.getType(), parkingSpotEntity.getType());
    }

    @Test
    void testParkingSpotEntityMap_WhenParkingSpotIsNull_ShouldReturnNullParkingSpotEntity() {
        ParkingSpotEntity parkingSpotEntity = daoMapper.map((ParkingSpot) null);

        assertNull(parkingSpotEntity);
    }

    @Test
    void testWorkingTimeEntityMap_WhenWorkingTimeIsValid_ShouldReturnWorkingTimeEntity() {
        ParkingLot parkingLot = ParkingLot.builder()
                .id(1)
                .name("Parking Lot from test")
                .address("Random Street")
                .startTime(LocalTime.of(6, 0, 0))
                .endTime(LocalTime.of(21, 0, 0))
                .state(true)
                .build();
        WorkingTime workingTime = WorkingTime.builder()
                .parkingLot(parkingLot)
                .nameDay("monday")
                .build();

        WorkingTimeEntity workingTimeEntity = daoMapper.map(workingTime);

        assertEquals(workingTime.getNameDay(), workingTimeEntity.getNameDay());
    }

    @Test
    void testWorkingTimeEntityMap_WhenWorkingTimeIsNull_ShouldReturnNullWorkingTimeEntity() {
        WorkingTimeEntity workingTimeEntity = daoMapper.map((WorkingTime) null);

        assertNull(workingTimeEntity);
    }

    @Test
    void testParkingLotMap_WhenParkingLotEntityHasNoUser_ShouldReturnParkingLot() {
        ParkingLotEntity parkingLotEntity = new ParkingLotEntity(1, "Parking Lot from test", "Random Street", LocalTime.of(6, 0, 0), LocalTime.of(21, 0, 0), true, null);

        ParkingLot parkingLot = daoMapper.map(parkingLotEntity);

        assertEquals(parkingLotEntity.getName(), parkingLot.getName());
        assertEquals(parkingLotEntity.getAddress(), parkingLot.getAddress());
        assertEquals(parkingLotEntity.getStartTime(), parkingLot.getStartTime());
        assertEquals(parkingLotEntity.getEndTime(), parkingLot.getEndTime());
        assertEquals(parkingLotEntity.getState(), parkingLot.isState());
    }

    @Test
    void testParkingLotMap_WhenParkingLotEntityIsValid_ShouldReturnParkingLot() {
        UserEntity userEntity = UserEntity.builder()
                .id(1)
                .name("username")
                .phone("063251148")
                .build();
        ParkingLotEntity parkingLotEntity = new ParkingLotEntity(1, "Parking Lot from test", "Random Street", LocalTime.of(6, 0, 0), LocalTime.of(21, 0, 0), true, Set.of(userEntity));

        ParkingLot parkingLot = daoMapper.map(parkingLotEntity);

        assertEquals(parkingLotEntity.getName(), parkingLot.getName());
        assertEquals(parkingLotEntity.getAddress(), parkingLot.getAddress());
        assertEquals(parkingLotEntity.getStartTime(), parkingLot.getStartTime());
        assertEquals(parkingLotEntity.getEndTime(), parkingLot.getEndTime());
        assertEquals(parkingLotEntity.getState(), parkingLot.isState());
        Set<User> users = parkingLot.getUsers();

        User mappedUser = users.iterator().next();
        assertEquals(userEntity.getId(), mappedUser.getId());
        assertEquals(userEntity.getName(), mappedUser.getName());
        assertEquals(userEntity.getPhone(), mappedUser.getPhone());
    }

    @Test
    void testParkingLotMap_WhenParkingLotEntityIsNull_ShouldReturnNullParkingLot() {
        ParkingLot parkingLot = daoMapper.map((ParkingLotEntity) null);

        assertNull(parkingLot);
    }

    @Test
    void testParkingLevelMap_WhenParkingLevelEntityIsValid_WithoutParkingSpots_ShouldReturnParkingLevel() {
        ParkingLevelEntity parkingLevelEntity = ParkingLevelEntity.builder()
                .id(1)
                .floor(1)
                .totalSpots(1)
                .parkingSpots(null)
                .build();

        ParkingLevel parkingLevel = daoMapper.map(parkingLevelEntity);

        assertEquals(parkingLevelEntity.getFloor(), parkingLevel.getFloor());
        assertEquals(parkingLevelEntity.getTotalSpots(), parkingLevel.getTotalSpots());
    }

    @Test
    void testParkingLevelMap_WhenParkingLevelEntityIsValid_ShouldReturnParkingLevel() {
        ParkingSpotEntity parkingSpotEntity = new ParkingSpotEntity(1, null, "A-001", false, "regular", null);
        ParkingLevelEntity parkingLevelEntity = ParkingLevelEntity.builder()
                .id(1)
                .floor(1)
                .totalSpots(1)
                .parkingSpots(Set.of(parkingSpotEntity))
                .build();

        ParkingLevel parkingLevel = daoMapper.map(parkingLevelEntity);

        assertEquals(parkingLevelEntity.getFloor(), parkingLevel.getFloor());
        assertEquals(parkingLevelEntity.getTotalSpots(), parkingLevel.getTotalSpots());

        Set<ParkingSpot> parkingSpots = parkingLevel.getParkingSpots();
        assertEquals(parkingLevelEntity.getParkingSpots().size(), parkingSpots.size());
    }

    @Test
    void testParkingLevelMap_WhenParkingLevelEntityIsNull_ShouldReturnNullParkingLevel() {
        ParkingLevel parkingLevel = daoMapper.map((ParkingLevelEntity) null);

        assertNull(parkingLevel);
    }

    @Test
    void testSetWorkingTime_WhenSetWorkingTimeEntitiesIsNull_ShouldReturnNullSetWorkingTime() {
        Set<WorkingTime> workingTimes = daoMapper.map((Set<WorkingTimeEntity>) null);

        assertNull(workingTimes);
    }

    @Test
    void testSetWorkingTime_WhenSetWorkingTimeEntitiesIsValid_ShouldReturnSetWorkingTime() {
        WorkingTimeEntity workingTimeEntity = new WorkingTimeEntity();
        workingTimeEntity.setId(1);
        workingTimeEntity.setNameDay("monday");

        Set<WorkingTime> workingTimes = daoMapper.map(Set.of(workingTimeEntity));
        WorkingTime workingTime = workingTimes.iterator().next();

        assertEquals("monday", workingTime.getNameDay());
    }

    @Test
    void testSetWorkingTime_WhenSetWorkingTimeEntitiesContainsNull_ShouldReturnNullSetWorkingTime() {
        Set<WorkingTimeEntity> workingTimeEntities = new HashSet<>();
        workingTimeEntities.add(null);

        Set<WorkingTime> workingTimes = daoMapper.map(workingTimeEntities);

        for (WorkingTime workingTime : workingTimes) {
            assertNull(workingTime);
        }
    }
}