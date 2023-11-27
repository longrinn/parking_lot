package com.endava.internship.dao.entity;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.endava.internship.dao.repository.ParkingLotRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class ParkingLotConstraintsTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    private static LocalTime START_TIME = LocalTime.of(8, 0);
    private static LocalTime END_TIME = LocalTime.of(20, 0);

    @Test
    public void parkingLotIsValid_thenSuccess() {
        final ParkingLotEntity parking_lot_entity = new ParkingLotEntity(null, "Lot 1", "123 Main St", START_TIME, END_TIME, true, null, null, null);
        parkingLotRepository.save(parking_lot_entity);

        final Optional<ParkingLotEntity> found = parkingLotRepository.findById(parking_lot_entity.getId());
        assertTrue(found.isPresent());
    }

    @Test
    public void nameIsNull_thenThrowException() {
        final ParkingLotEntity parking_lot_entity = new ParkingLotEntity(null, null, "123 Main St", START_TIME, END_TIME, true, null, null, null);

        assertThrows(ConstraintViolationException.class, () -> {
            parkingLotRepository.save(parking_lot_entity);
        });
    }

    @Test
    public void addressIsNull_thenThrowException() {
        final ParkingLotEntity parking_lot_entity = new ParkingLotEntity(null, "Lot 1", null, START_TIME, END_TIME, true, null, null, null);

        assertThrows(ConstraintViolationException.class, () -> {
            parkingLotRepository.save(parking_lot_entity);
        });
    }

    @Test
    public void startTimeIsNull_thenThrowException() {
        final ParkingLotEntity parking_lot_entity = new ParkingLotEntity(null, "Lot 1", "123 Main St", null, END_TIME, true, null, null, null);

        assertThrows(ConstraintViolationException.class, () -> {
            parkingLotRepository.save(parking_lot_entity);
        });
    }

    @Test
    public void endTimeIsNull_thenThrowException() {
        final ParkingLotEntity parking_lot_entity = new ParkingLotEntity(null, "Lot 1", "123 Main St", START_TIME, null, true,null, null, null);

        assertThrows(ConstraintViolationException.class, () -> {
            parkingLotRepository.save(parking_lot_entity);
        });
    }

    @Test
    public void workingTimesAreHandledCorrectly() {
        final ParkingLotEntity parking_lot_entity = new ParkingLotEntity(null, "Lot 1", "123 Main St", START_TIME, END_TIME, true, null, null, null);
        parkingLotRepository.save(parking_lot_entity);

        WorkingTimeEntity workingTime = new WorkingTimeEntity();
        workingTime.setNameDay("Monday");
        workingTime.setParkingLot(parking_lot_entity);
        entityManager.persist(workingTime);

        Set<WorkingTimeEntity> workingTimes = new HashSet<>();
        workingTimes.add(workingTime);
        parking_lot_entity.setWorkingTimes(workingTimes);

        final Optional<ParkingLotEntity> foundOptional = parkingLotRepository.findById(parking_lot_entity.getId());

        final ParkingLotEntity found = foundOptional.get();
        assertNotNull(found);
        assertNotNull(found.getWorkingTimes());
        assertFalse(found.getWorkingTimes().isEmpty());
    }

    @Test
    public void updateParkingLotEntity_thenSuccess() {
        final ParkingLotEntity parking_lot_entity = new ParkingLotEntity(null, "Lot 1", "123 Main St", START_TIME, END_TIME, true, null, null, null);

        parkingLotRepository.save(parking_lot_entity);
        final ParkingLotEntity savedEntity = parkingLotRepository.findById(parking_lot_entity.getId()).get();

        savedEntity.setName("Updated Lot Name");
        savedEntity.setAddress("456 Another St");
        savedEntity.setStartTime(LocalTime.of(9, 0));
        savedEntity.setEndTime(LocalTime.of(21, 0));

        parkingLotRepository.save(savedEntity);

        final ParkingLotEntity updatedEntity = parkingLotRepository.findById(parking_lot_entity.getId()).get();

        assertEquals("Updated Lot Name", updatedEntity.getName());
        assertEquals("456 Another St", updatedEntity.getAddress());
        assertEquals(LocalTime.of(9, 0), updatedEntity.getStartTime());
        assertEquals(LocalTime.of(21, 0), updatedEntity.getEndTime());
    }

    @Test
    public void whenSavingDuplicateName_thenThrowException() {
        final ParkingLotEntity parking_lot_entity = new ParkingLotEntity(null, "Lot 1", "123 Main St", START_TIME, END_TIME, true, null, null, null);

        parkingLotRepository.save(parking_lot_entity);
        final ParkingLotEntity duplicateNameEntity = new ParkingLotEntity(null, "Lot 1", "Different Address", START_TIME, END_TIME, true, null, null, null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            parkingLotRepository.save(duplicateNameEntity);
        });
    }

    @Test
    public void whenSavingDuplicateAddress_thenThrowException() {
        final ParkingLotEntity parking_lot_entity = new ParkingLotEntity(null, "Lot 1", "123 Main St", START_TIME, END_TIME, true, null, null, null);

        parkingLotRepository.save(parking_lot_entity);
        final ParkingLotEntity duplicateAddressEntity = new ParkingLotEntity(null, "Different Name", "123 Main St", START_TIME, END_TIME, true, null, null, null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            parkingLotRepository.save(duplicateAddressEntity);
        });
    }

    @Test
    public void whenDeletedParkingLot_thenItIsNoLongerAvailable() {
        final ParkingLotEntity parking_lot_entity = new ParkingLotEntity(null, "Lot 1", "123 Main St", START_TIME, END_TIME, true, null, null, null);

        parkingLotRepository.save(parking_lot_entity);
        assertTrue(parkingLotRepository.findById(parking_lot_entity.getId()).isPresent());

        parkingLotRepository.delete(parking_lot_entity);
        assertFalse(parkingLotRepository.findById(parking_lot_entity.getId()).isPresent());
    }
}