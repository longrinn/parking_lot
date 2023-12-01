package com.endava.internship.dao.entity;

import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.endava.internship.dao.repository.ParkingLevelRepository;
import com.endava.internship.dao.repository.ParkingLotRepository;
import com.endava.internship.dao.repository.ParkingSpotRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class ParkingSpotConstraintsTest {

    private static ParkingLotEntity PARKING_LOT_ENTITY;
    private static ParkingSpotEntity PARKING_SPOT_ENTITY;
    private static ParkingLevelEntity PARKING_LEVEL_ENTITY;
    @Autowired
    private ParkingSpotRepository parkingSpotRepository;
    @Autowired
    private ParkingLevelRepository parkingLevelRepository;
    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @BeforeEach
    public void setUp() {
        PARKING_LOT_ENTITY = new ParkingLotEntity(null, "Lot 1", "123 Main St", LocalTime.of(8, 0), LocalTime.of(20, 0), true, null);
        parkingLotRepository.save(PARKING_LOT_ENTITY);
        PARKING_LEVEL_ENTITY = new ParkingLevelEntity(null, PARKING_LOT_ENTITY, 1, 50, null);
        parkingLevelRepository.save(PARKING_LEVEL_ENTITY);

        PARKING_SPOT_ENTITY = new ParkingSpotEntity();
        PARKING_SPOT_ENTITY.setAvailable(true);
        PARKING_SPOT_ENTITY.setName("A-001");
        PARKING_SPOT_ENTITY.setType("Regular");
        PARKING_SPOT_ENTITY.setParkingLevel(PARKING_LEVEL_ENTITY);
        PARKING_SPOT_ENTITY.setUser(null);
    }

    @Test
    public void parkingSpotIsValid_thenSuccess() {
        parkingSpotRepository.save(PARKING_SPOT_ENTITY);
        final Optional<ParkingSpotEntity> found = parkingSpotRepository.findById(PARKING_SPOT_ENTITY.getId());

        assertTrue(found.isPresent());
        assertNull(found.get().getUser());
        assertEquals(PARKING_LEVEL_ENTITY, found.get().getParkingLevel());
    }

    @Test
    public void whenNameIsNull_thenThrowException() {
        PARKING_SPOT_ENTITY.setName(null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            parkingSpotRepository.save(PARKING_SPOT_ENTITY);
        });
    }

    @Test
    public void whenTypeIsNull_thenThrowException() {
        PARKING_SPOT_ENTITY.setType(null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            parkingSpotRepository.save(PARKING_SPOT_ENTITY);
        });
    }

    @Test
    public void whenUpdatedParkingSpot_thenChangesArePersisted() {
        parkingSpotRepository.save(PARKING_SPOT_ENTITY);
        final ParkingSpotEntity savedEntity = parkingSpotRepository.findById(PARKING_SPOT_ENTITY.getId()).get();

        savedEntity.setName("A-002");
        savedEntity.setAvailable(false);
        savedEntity.setType("Family");

        parkingSpotRepository.save(savedEntity);
        final ParkingSpotEntity updatedEntity = parkingSpotRepository.findById(PARKING_SPOT_ENTITY.getId()).get();

        assertEquals("A-002", updatedEntity.getName());
        assertFalse(updatedEntity.isAvailable());
        assertEquals("Family", updatedEntity.getType());
    }

    @Test
    public void whenDeletedParkingSpot_thenItIsNoLongerAvailable() {
        parkingSpotRepository.save(PARKING_SPOT_ENTITY);
        assertTrue(parkingSpotRepository.findById(PARKING_SPOT_ENTITY.getId()).isPresent());

        parkingSpotRepository.delete(PARKING_SPOT_ENTITY);
        assertFalse(parkingSpotRepository.findById(PARKING_SPOT_ENTITY.getId()).isPresent());
    }
}