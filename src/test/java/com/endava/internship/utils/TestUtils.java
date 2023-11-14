package com.endava.internship.utils;

import com.endava.internship.dao.entity.CredentialsEntity;
import com.endava.internship.dao.entity.RoleEntity;
import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.infrastructure.security.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;

public class TestUtils {

    public static final String EXISTING_USER_EMAIL = "user@example.com";
    public static final String NON_EXISTENT_USER_EMAIL = "nonexistent_user@example.com";
    public static final String ROLE_USER = "USER";
    public static final String PHONE_NUMBER = "37369583920";

    public static String getUserEmail(UserDetails userDetails) {
        return getUserFromDetails(userDetails).getCredential().getEmail();
    }

    public static String getUserRole(UserDetails userDetails) {
        return getUserFromDetails(userDetails).getRole().getName();
    }

    public static String getUserPhone(UserDetails userDetails) {
        return getUserFromDetails(userDetails).getPhone();
    }

    private static UserEntity getUserFromDetails(UserDetails userDetails) {
        return ((UserDetailsImpl) userDetails).getUser();
    }

    public static UserEntity setupExistingUser() {
        CredentialsEntity credentialsEntity = new CredentialsEntity();
        credentialsEntity.setEmail(EXISTING_USER_EMAIL);
        credentialsEntity.setPassword("password");

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(ROLE_USER);

        UserEntity userEntity = new UserEntity();
        userEntity.setName("Name");
        userEntity.setPhone(PHONE_NUMBER);
        userEntity.setCredential(credentialsEntity);
        userEntity.setRole(roleEntity);

        return userEntity;
    }
}
