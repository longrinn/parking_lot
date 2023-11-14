package com.endava.internship.dao.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class RoleConstraintsTest {

    @PersistenceContext
    private EntityManager entityManager;

    private static RoleEntity ROLE_ENTITY;

    @BeforeEach
    public void set_up() {
        ROLE_ENTITY = new RoleEntity();
    }
    @Test
    public void roleIsValid_thenSuccess() {
        ROLE_ENTITY.setId(3);
        ROLE_ENTITY.setName("SuperAdmin");

        assertDoesNotThrow(() -> {
            entityManager.persist(ROLE_ENTITY);
            entityManager.flush();
        });

        final RoleEntity found = entityManager.find(RoleEntity.class, ROLE_ENTITY.getId());
        assertNotNull(found);
    }

    @Test
    public void nameIsNull_thenThrowException() {
        ROLE_ENTITY.setId(3);
        ROLE_ENTITY.setName(null);

        assertThrows(PersistenceException.class, () -> {
            entityManager.persist(ROLE_ENTITY);
            entityManager.flush();
        });
    }

    @Test
    public void nameIsDuplicate_thenThrowException() {
        ROLE_ENTITY.setId(3);
        ROLE_ENTITY.setName("User");
        entityManager.persist(ROLE_ENTITY);

        RoleEntity role2 = new RoleEntity();
        role2.setId(4);
        role2.setName("User");

        assertThrows(PersistenceException.class, () -> {
            entityManager.persist(role2);
            entityManager.flush();
        });
    }

    @Test
    public void idIsDuplicate_thenThrowException() {
        ROLE_ENTITY.setId(5);
        ROLE_ENTITY.setName("Role1");
        entityManager.persist(ROLE_ENTITY);

        RoleEntity role2 = new RoleEntity();
        role2.setId(5);
        role2.setName("Role2");

        assertThrows(PersistenceException.class, () -> {
            entityManager.persist(role2);
            entityManager.flush();
        });
    }

    @Test
    public void idIsNull_thenThrowException() {
        ROLE_ENTITY.setId(null);
        ROLE_ENTITY.setName("Role1");

        assertThrows(PersistenceException.class, () -> {
            entityManager.persist(ROLE_ENTITY);
            entityManager.flush();
        });
    }
}