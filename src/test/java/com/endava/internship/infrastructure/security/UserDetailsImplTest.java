package com.endava.internship.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.endava.internship.dao.entity.CredentialsEntity;
import com.endava.internship.dao.entity.RoleEntity;
import com.endava.internship.dao.entity.UserEntity;

class UserDetailsImplTest {

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setup() {
        CredentialsEntity credentials = CredentialsEntity.builder()
                .id(51)
                .email("user@mail.com")
                .password("Secret#51")
                .build();
        RoleEntity role = new RoleEntity(2, "Admin");
        UserEntity user = new UserEntity(51, credentials, "VeryNonSusUser", "051515151", role, null,null);
        userDetails = new UserDetailsImpl(user);
    }

    @Test
    void getPassword_ShouldReturnActualPassword() {
        assertEquals("Secret#51", userDetails.getPassword());
    }

    @Test
    void isAccountNonExpired_ShouldReturnFalse() {
        assertFalse(userDetails.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_ShouldReturnFalse() {
        assertFalse(userDetails.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_ShouldReturnFalse() {
        assertFalse(userDetails.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_ShouldReturnFalse() {
        assertFalse(userDetails.isEnabled());
    }
}