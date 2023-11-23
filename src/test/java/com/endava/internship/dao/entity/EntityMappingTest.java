package com.endava.internship.dao.entity;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class EntityMappingTest {

    @PersistenceContext
    private EntityManager entityManager;
    private static ParkingLotEntity PARKING_LOT;
    private static ParkingLevelEntity PARKING_LEVEL;
    private static ParkingSpotEntity PARKING_SPOT;
    private static WorkingTimeEntity WORKING_TIME;

    @BeforeEach
    public void setup() {
        PARKING_LOT = new ParkingLotEntity(null, "Lot 1", "123 Main St", LocalTime.of(8, 0), LocalTime.of(20, 0), null, null);
        entityManager.persist(PARKING_LOT);

        PARKING_LEVEL = new ParkingLevelEntity(null, PARKING_LOT, 1, 50, null);
        entityManager.persist(PARKING_LEVEL);

        PARKING_SPOT = new ParkingSpotEntity(null, PARKING_LEVEL, "A1", true, "Regular", null);
        entityManager.persist(PARKING_SPOT);

        WORKING_TIME = new WorkingTimeEntity(null, PARKING_LOT, "Monday");
        entityManager.persist(WORKING_TIME);

        entityManager.flush();
    }

    @Test
    public void testUserEntityMapping() {
        final RoleEntity userRole = entityManager.find(RoleEntity.class, 2);

        final UserEntity user = new UserEntity(null, null, "John Doe", "045678904", userRole, null);
        entityManager.persist(user);

        final CredentialsEntity credentials = new CredentialsEntity(null, user, "john.doe@example.com", "password123");
        user.setCredential(credentials);
        entityManager.persist(credentials);

        entityManager.flush();
        entityManager.clear();

        final UserEntity retrievedUser = entityManager.find(UserEntity.class, user.getId());
        assertNotNull(retrievedUser);
        assertNotNull(retrievedUser.getCredential());
        assertEquals("john.doe@example.com", retrievedUser.getCredential().getEmail());
    }

    @Test
    public void testParkingLotAndWorkingTimesRelationship() {
        final Set<WorkingTimeEntity> workingTimes = new HashSet<>();
        workingTimes.add(WORKING_TIME);
        PARKING_LOT.setWorkingTimes(workingTimes);

        entityManager.flush();
        entityManager.clear();

        final ParkingLotEntity retrievedLot = entityManager.find(ParkingLotEntity.class, PARKING_LOT.getId());
        assertNotNull(retrievedLot);
        assertEquals(1, retrievedLot.getWorkingTimes().size());
        assertEquals("Monday", retrievedLot.getWorkingTimes().iterator().next().getNameDay());

        final WorkingTimeEntity retrievedWorkingTime = entityManager.find(WorkingTimeEntity.class, WORKING_TIME.getId());
        assertNotNull(retrievedWorkingTime);
        assertEquals("Lot 1", retrievedWorkingTime.getParkingLot().getName());
        assertEquals("Monday", retrievedWorkingTime.getNameDay());
    }

    @Test
    public void testParkingLotAndParkingLevelRelationship() {
        final Set<ParkingLevelEntity> parkingLevels = new HashSet<>();
        parkingLevels.add(PARKING_LEVEL);
        PARKING_LOT.setParkingLevels(parkingLevels);

        entityManager.flush();
        entityManager.clear();

        final ParkingLotEntity retrievedLot = entityManager.find(ParkingLotEntity.class, PARKING_LOT.getId());
        assertNotNull(retrievedLot);
        assertEquals(1, retrievedLot.getParkingLevels().size());

        final ParkingLevelEntity retrievedLevel = retrievedLot.getParkingLevels().iterator().next();
        assertEquals(1, retrievedLevel.getFloor());
        assertEquals("Lot 1", retrievedLevel.getParkingLot().getName());
    }

    @Test
    public void testParkingLevelAndParkingSpotRelationship() {
        final Set<ParkingSpotEntity> parkingSpots = new HashSet<>();
        parkingSpots.add(PARKING_SPOT);
        PARKING_LEVEL.setParkingSpots(parkingSpots);

        entityManager.flush();
        entityManager.clear();

        final ParkingLevelEntity retrievedLevel = entityManager.find(ParkingLevelEntity.class, PARKING_LEVEL.getId());
        assertNotNull(retrievedLevel);
        assertEquals(1, retrievedLevel.getParkingSpots().size());

        final ParkingSpotEntity retrievedSpot = retrievedLevel.getParkingSpots().iterator().next();
        assertEquals("A1", retrievedSpot.getName());
        assertTrue(retrievedSpot.getState());
        assertEquals("Lot 1", retrievedSpot.getParkingLevel().getParkingLot().getName());
    }

    @Test
    public void testUserAndParkingSpotRelationship() {
        final UserEntity user = new UserEntity();
        user.setName("John Doe");
        user.setPhone("1234567890");
        entityManager.persist(user);

        final ParkingSpotEntity parkingSpot = new ParkingSpotEntity();
        parkingSpot.setName("A1");
        parkingSpot.setState(true);
        parkingSpot.setType("Regular");
        parkingSpot.setUser(user);
        parkingSpot.setParkingLevel(PARKING_LEVEL);
        entityManager.persist(parkingSpot);

        user.setParking_spot(parkingSpot);

        entityManager.flush();
        entityManager.clear();

        final UserEntity retrievedUser = entityManager.find(UserEntity.class, user.getId());
        assertNotNull(retrievedUser);
        assertNotNull(retrievedUser.getParking_spot());
        assertEquals("A1", retrievedUser.getParking_spot().getName());

        final ParkingSpotEntity retrievedSpot = entityManager.find(ParkingSpotEntity.class, parkingSpot.getId());
        assertNotNull(retrievedSpot);
        assertNotNull(retrievedSpot.getUser());
        assertEquals("John Doe", retrievedSpot.getUser().getName());
    }
}