package com.endava.internship.dao.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;

@DataJpaTest
@ActiveProfiles("test")
public class UserConstraintsTest {
    @PersistenceContext
    private EntityManager entityManager;
    private static RoleEntity ROLE_ADMIN;
    private static UserEntity USER_ENTITY;

    @BeforeEach
    public void setUp() {
        ROLE_ADMIN = entityManager.find(RoleEntity.class, 1);
        USER_ENTITY = new UserEntity();
    }

    @Test
    public void userIsValid_thenSuccess() {
        USER_ENTITY.setName("John Doe");
        USER_ENTITY.setPhone("+37312345678");
        USER_ENTITY.setRole(ROLE_ADMIN);

        entityManager.persist(USER_ENTITY);
        entityManager.flush();

        final UserEntity found = entityManager.find(UserEntity.class, USER_ENTITY.getId());
        assertNotNull(found);
    }

    @Test
    public void userWithCredentialsIsValid_thenSuccess() {
        USER_ENTITY.setName("John Doe");
        USER_ENTITY.setPhone("+37312345679");
        USER_ENTITY.setRole(ROLE_ADMIN);

        final CredentialsEntity credentials = new CredentialsEntity();
        credentials.setEmail("john@example.com");
        credentials.setPassword("StrongPassword123");

        USER_ENTITY.setCredential(credentials);
        credentials.setUserEntity(USER_ENTITY);

        entityManager.persist(USER_ENTITY);
        entityManager.flush();

        final CredentialsEntity foundCredentials = entityManager.find(CredentialsEntity.class, credentials.getId());
        assertNotNull(foundCredentials);
        assertEquals(foundCredentials.getUserEntity().getId(), USER_ENTITY.getId());
    }

    @Test
    public void whenNameIsNull_thenThrowException() {
        USER_ENTITY.setName(null);

        assertThrows(PersistenceException.class, () -> {
            entityManager.persist(USER_ENTITY);
            entityManager.flush();
        });
    }

    @Test
    public void whenPhoneIsDuplicate_thenThrowException() {
        USER_ENTITY.setName("Alice");
        USER_ENTITY.setPhone("+37312345679");
        USER_ENTITY.setRole(ROLE_ADMIN);

        entityManager.persist(USER_ENTITY);
        entityManager.flush();

        UserEntity user2 = new UserEntity();
        user2.setName("Bob");
        user2.setPhone("+37312345679");
        user2.setRole(ROLE_ADMIN);

        assertThrows(PersistenceException.class, () -> {
            entityManager.persist(user2);
            entityManager.flush();
        });
    }
}