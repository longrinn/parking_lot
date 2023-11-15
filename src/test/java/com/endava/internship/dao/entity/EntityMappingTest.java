package com.endava.internship.dao.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class EntityMappingTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testUserEntityMapping() {
        RoleEntity userRole = entityManager.find(RoleEntity.class, 2);

        UserEntity user = new UserEntity(null, null, "John Doe", "+37345678904", userRole);
        entityManager.persist(user);

        CredentialsEntity credentials = new CredentialsEntity(null, user, "john.doe@example.com", "password123");
        user.setCredential(credentials);
        entityManager.persist(credentials);

        entityManager.flush();
        entityManager.clear();

        UserEntity retrievedUser = entityManager.find(UserEntity.class, user.getId());
        assertNotNull(retrievedUser);
        assertNotNull(retrievedUser.getRole());
        assertEquals("User", retrievedUser.getRole().getName());
        assertNotNull(retrievedUser.getCredential());
        assertEquals("john.doe@example.com", retrievedUser.getCredential().getEmail());
    }
}