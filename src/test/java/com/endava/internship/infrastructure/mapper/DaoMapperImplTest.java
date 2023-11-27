package com.endava.internship.infrastructure.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.endava.internship.dao.entity.CredentialsEntity;
import com.endava.internship.dao.entity.RoleEntity;
import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.infrastructure.domain.Credentials;
import com.endava.internship.infrastructure.domain.Role;
import com.endava.internship.infrastructure.domain.User;

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
}