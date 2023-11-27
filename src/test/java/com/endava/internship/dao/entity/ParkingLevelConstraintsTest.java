package com.endava.internship.dao.entity;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.endava.internship.dao.repository.ParkingLevelRepository;
import com.endava.internship.dao.repository.ParkingLotRepository;
import com.endava.internship.dao.repository.ParkingSpotRepository;

import jakarta.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class ParkingLevelConstraintsTest {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private ParkingLevelRepository parkingLevelRepository;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    private static ParkingLotEntity PARKING_LOT_ENTITY;
    private static ParkingLevelEntity PARKING_LEVEL_ENTITY;

    @BeforeEach
    public void setUp() {
        PARKING_LOT_ENTITY = new ParkingLotEntity(null, "Lot 1", "123 Main St", LocalTime.of(8, 0), LocalTime.of(20, 0), true, null, null, null);
        parkingLotRepository.save(PARKING_LOT_ENTITY);

        PARKING_LEVEL_ENTITY = new ParkingLevelEntity();
        PARKING_LEVEL_ENTITY.setParkingLot(PARKING_LOT_ENTITY);
    }

    @Test
    public void parkingLevelIsValid_thenSuccess() {
        PARKING_LEVEL_ENTITY.setFloor(1);
        PARKING_LEVEL_ENTITY.setTotalSpots(50);

        parkingLevelRepository.save(PARKING_LEVEL_ENTITY);

        final Optional<ParkingLevelEntity> found = parkingLevelRepository.findById(PARKING_LEVEL_ENTITY.getId());

        assertNotNull(found);
        assertEquals(PARKING_LOT_ENTITY, found.get().getParkingLot());
    }

    @Test
    public void floorIsNull_thenThrowException() {
        PARKING_LEVEL_ENTITY.setTotalSpots(50);
        PARKING_LEVEL_ENTITY.setFloor(null);

        assertThrows(ConstraintViolationException.class, () -> {
            parkingLevelRepository.save(PARKING_LEVEL_ENTITY);
        });
    }

    @Test
    public void totalSpotsIsNull_thenThrowException() {
        PARKING_LEVEL_ENTITY.setFloor(1);
        PARKING_LEVEL_ENTITY.setTotalSpots(null);

        assertThrows(ConstraintViolationException.class, () -> {
            parkingLevelRepository.save(PARKING_LEVEL_ENTITY);
        });
    }

    @Test
    public void parkingLotIsNull_thenThrowException() {
        PARKING_LEVEL_ENTITY.setFloor(1);
        PARKING_LEVEL_ENTITY.setTotalSpots(50);
        PARKING_LEVEL_ENTITY.setParkingLot(null);

        assertThrows(ConstraintViolationException.class, () -> {
            parkingLevelRepository.save(PARKING_LEVEL_ENTITY);
        });
    }

    @Test
    public void parkingSpotsAreHandledCorrectly_thenSuccess() {
        PARKING_LEVEL_ENTITY.setFloor(1);
        PARKING_LEVEL_ENTITY.setTotalSpots(50);

        ParkingSpotEntity parkingSpot = new ParkingSpotEntity();
        parkingSpot.setName("A1");
        parkingSpot.setState(true);
        parkingSpot.setType("Regular");
        parkingSpot.setParkingLevel(PARKING_LEVEL_ENTITY);

        Set<ParkingSpotEntity> parkingSpots = new HashSet<>();
        parkingSpots.add(parkingSpot);
        PARKING_LEVEL_ENTITY.setParkingSpots(parkingSpots);

        parkingLevelRepository.save(PARKING_LEVEL_ENTITY);
        parkingSpotRepository.save(parkingSpot);

        final Optional<ParkingLevelEntity> foundOptional = parkingLevelRepository.findById(PARKING_LEVEL_ENTITY.getId());

        final ParkingLevelEntity found = foundOptional.get();
        assertNotNull(found);
        assertNotNull(found.getParkingSpots());
        assertFalse(found.getParkingSpots().isEmpty());
    }

    @Test
    public void updateParkingLevelEntity_thenSuccess() {
        PARKING_LEVEL_ENTITY.setFloor(1);
        PARKING_LEVEL_ENTITY.setTotalSpots(50);
        parkingLevelRepository.save(PARKING_LEVEL_ENTITY);

        ParkingLevelEntity savedEntity = parkingLevelRepository.findById(PARKING_LEVEL_ENTITY.getId()).get();
        savedEntity.setFloor(2);
        savedEntity.setTotalSpots(100);
        parkingLevelRepository.save(savedEntity);

        final ParkingLevelEntity updatedEntity = parkingLevelRepository.findById(PARKING_LEVEL_ENTITY.getId()).get();
        assertNotNull(updatedEntity);
        assertEquals(2, updatedEntity.getFloor());
        assertEquals(100, updatedEntity.getTotalSpots());
    }

    @Test
    public void whenDeletedParkingLot_thenItIsNoLongerAvailable() {
        PARKING_LEVEL_ENTITY.setFloor(1);
        PARKING_LEVEL_ENTITY.setTotalSpots(50);
        parkingLevelRepository.save(PARKING_LEVEL_ENTITY);
        assertTrue(parkingLevelRepository.findById(PARKING_LEVEL_ENTITY.getId()).isPresent());

        parkingLevelRepository.delete(PARKING_LEVEL_ENTITY);

        assertFalse(parkingLevelRepository.findById(PARKING_LEVEL_ENTITY.getId()).isPresent());
    }
}