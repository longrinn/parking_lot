package com.endava.internship.dao.entity;

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
public class CredentialsConstraintsTest {
    @PersistenceContext
    private EntityManager entityManager;
    private static UserEntity USER_ENTITY;
    private static CredentialsEntity CREDENTIALS_ENTITY;

    @BeforeEach
    public void setUp() {
        USER_ENTITY = new UserEntity();
        USER_ENTITY.setName("test");
        entityManager.persist(USER_ENTITY);

        CREDENTIALS_ENTITY = new CredentialsEntity();
    }

    @Test
    public void credentialsAreValid_thenSuccess() {
        CREDENTIALS_ENTITY.setUserEntity(USER_ENTITY);
        CREDENTIALS_ENTITY.setEmail("anotherValid@example.com");
        CREDENTIALS_ENTITY.setPassword("AnotherStrongPassword123");

        entityManager.persist(CREDENTIALS_ENTITY);
        entityManager.flush();

        final CredentialsEntity found = entityManager.find(CredentialsEntity.class, CREDENTIALS_ENTITY.getId());
        assertNotNull(found);
    }

    @Test
    public void passIsNull_thenThrowException() {
        CREDENTIALS_ENTITY.setPassword(null);

        assertThrows(PersistenceException.class, () -> {
            entityManager.persist(CREDENTIALS_ENTITY);
            entityManager.flush();
        });
    }

    @Test
    public void emailIsDuplicate_thenThrowException() {
        CREDENTIALS_ENTITY.setUserEntity(USER_ENTITY);
        CREDENTIALS_ENTITY.setEmail("duplicate@example.com");
        CREDENTIALS_ENTITY.setPassword("ValidPassword1");
        entityManager.persist(CREDENTIALS_ENTITY);
        entityManager.flush();

        UserEntity userEntity2 = new UserEntity();
        userEntity2.setName("test2");
        entityManager.persist(userEntity2);

        CredentialsEntity secondCredentials = new CredentialsEntity();
        secondCredentials.setUserEntity(userEntity2);
        secondCredentials.setEmail("duplicate@example.com");
        secondCredentials.setPassword("ValidPassword2");

        assertThrows(PersistenceException.class, () -> {
            entityManager.persist(secondCredentials);
            entityManager.flush();
        });
    }

    @Test
    public void userEntityIsNotSet_thenThrowException() {
        CREDENTIALS_ENTITY.setEmail("valid@example.com");
        CREDENTIALS_ENTITY.setPassword("ValidPassword");

        assertThrows(PersistenceException.class, () -> {
            entityManager.persist(CREDENTIALS_ENTITY);
            entityManager.flush();
        });
    }

    @Test
    public void userEntityDoesNotExist_thenThrowException() {
        UserEntity nonExistentUser = new UserEntity();
        nonExistentUser.setId(999);

        CREDENTIALS_ENTITY.setUserEntity(nonExistentUser);
        CREDENTIALS_ENTITY.setEmail("valid@example.com");
        CREDENTIALS_ENTITY.setPassword("ValidPassword");

        assertThrows(PersistenceException.class, () -> {
            entityManager.persist(CREDENTIALS_ENTITY);
            entityManager.flush();
        });
    }
}