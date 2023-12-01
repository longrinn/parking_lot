package com.endava.internship.dao.entity;

import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
public class WorkingTimeConstraintsTest {

    private static ParkingLotEntity PARKING_LOT_ENTITY;
    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        PARKING_LOT_ENTITY = new ParkingLotEntity(null, "Lot 1", "123 Main St", LocalTime.of(8, 0), LocalTime.of(20, 0), true, null);
        entityManager.persist(PARKING_LOT_ENTITY);
    }

    @Test
    public void whenWorkingTimeIsValid_thenSuccess() {
        WorkingTimeEntity workingTime = new WorkingTimeEntity(null, PARKING_LOT_ENTITY, "Monday");
        entityManager.persist(workingTime);

        final WorkingTimeEntity found = entityManager.find(WorkingTimeEntity.class, workingTime.getId());

        assertNotNull(found);
        assertEquals(PARKING_LOT_ENTITY, found.getParkingLot());
    }

    @Test
    public void whenNameDayIsNull_thenThrowException() {
        WorkingTimeEntity workingTime = new WorkingTimeEntity(null, PARKING_LOT_ENTITY, null);

        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> {
            entityManager.persist(workingTime);
        });
    }

    @Test
    public void whenParkingLotIsNull_thenThrowException() {
        WorkingTimeEntity workingTime = new WorkingTimeEntity(null, null, "Monday");

        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> {
            entityManager.persist(workingTime);
        });
    }

    @Test
    public void whenUpdatedWorkingTime_thenChangesArePersisted() {
        WorkingTimeEntity workingTime = new WorkingTimeEntity(null, PARKING_LOT_ENTITY, "Monday");
        entityManager.persist(workingTime);

        workingTime.setNameDay("Tuesday");
        entityManager.persist(workingTime);

        final WorkingTimeEntity updatedEntity = entityManager.find(WorkingTimeEntity.class, workingTime.getId());
        assertEquals("Tuesday", updatedEntity.getNameDay());
    }

    @Test
    public void whenDeletedWorkingTime_thenItIsNoLongerAvailable() {
        WorkingTimeEntity workingTime = new WorkingTimeEntity(null, PARKING_LOT_ENTITY, "Monday");
        entityManager.persist(workingTime);

        final WorkingTimeEntity savedEntity = entityManager.find(WorkingTimeEntity.class, workingTime.getId());
        assertEquals(savedEntity, workingTime);

        entityManager.remove(workingTime);
        assertNull(entityManager.find(WorkingTimeEntity.class, workingTime.getId()));
    }
}