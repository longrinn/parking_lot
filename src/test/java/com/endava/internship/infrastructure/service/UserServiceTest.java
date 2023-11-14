package com.endava.internship.infrastructure.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.endava.internship.dao.entity.CredentialsEntity;
import com.endava.internship.dao.entity.RoleEntity;
import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.dao.repository.CredentialRepository;
import com.endava.internship.dao.repository.RoleRepository;
import com.endava.internship.dao.repository.UserRepository;
import com.endava.internship.infrastructure.domain.Role;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.infrastructure.listeners.UserRoleChangeEmailListener;
import com.endava.internship.infrastructure.mapper.DaoMapper;
import com.endava.internship.infrastructure.mapper.DtoMapper;
import com.endava.internship.web.request.ChangeRoleRequest;

import jakarta.persistence.EntityNotFoundException;

@Import(UserServiceImpl.class)
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private DaoMapper daoMapper;

    @Mock
    private DtoMapper dtoMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    CredentialRepository credentialRepository;
    @Mock
    UserRoleChangeEmailListener userRoleChangeEmailListener;

    @InjectMocks
    private UserServiceImpl userService;
    ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);


    @Test
    void updateUserRoleUserRoleShouldBeUpdated() {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(1, "Admin");
        RoleEntity roleEntity = new RoleEntity(1, "User");
        UserEntity userEntity = new UserEntity(1, null, "John", "+37368521164", roleEntity);

        when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));
        when(roleRepository.findRoleEntityByName("Admin")).thenReturn(Optional.of(new RoleEntity(2, "Admin")));


        userService.updateUserRole(changeRoleRequest);

        verify(userRepository).findById(1);
        verify(roleRepository).findRoleEntityByName("Admin");

        ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userEntityCaptor.capture());

        UserEntity capturedUserEntity = userEntityCaptor.getValue();
        assertNotNull(capturedUserEntity);
        assertEquals("Admin", capturedUserEntity.getRole().getName());
    }


    @Test
    void updateUserRoleWhenUserIdIsNullShouldThrowException() {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(null, "Role");
        UserEntity user = new UserEntity(1, null, "John", "+37368521164", null);
        RoleEntity newRole = new RoleEntity(1, "Admin");
        user.setRole(newRole);

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserRole(changeRoleRequest));
    }

    @Test
    void updateUserRoleWhenRoleIdIsNullShouldThrowException() {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(1, null);
        UserEntity user = new UserEntity(1, null, "John", "+37368521164", null);
        RoleEntity newRole = new RoleEntity(1, "Admin");
        user.setRole(newRole);

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserRole(changeRoleRequest));
    }

    @Test
    void updateUserRoleWhenUserNotFoundShouldThrowException() {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(2, "Role");
        UserEntity user = new UserEntity(1, null, "John", "+37368521164", null);

        when(userRepository.findById(changeRoleRequest.getUserId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserRole(changeRoleRequest));
    }

    @Test
    void updateUserRoleWhenRoleNotFoundShouldThrowException() {
        final ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(1, "Role");
        UserEntity user = new UserEntity(1, null, "John", "+37368521164", null);
        RoleEntity newRole1 = new RoleEntity(1, "Admin");
        RoleEntity newRole2 = new RoleEntity(2, "User");

        when(userRepository.findById(changeRoleRequest.getUserId())).thenReturn(Optional.of(new UserEntity()));
        when(roleRepository.findRoleEntityByName(changeRoleRequest.getNewRole())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserRole(changeRoleRequest));
    }
    @Test
    void updateUserRole_WhenUserRoleIsUpdatedAndEmailSent_ShouldSucceed() {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(1, "Admin");
        RoleEntity roleEntity = new RoleEntity(1, "User");
        UserEntity userEntity = new UserEntity(1, null, "John", "+37368521164", roleEntity);
        CredentialsEntity credentialsEntity = new CredentialsEntity(1, userEntity, "test@example.com", "Password");
        Role role = new Role("Admin");
        User user = new User(1, "John", "+37368521164", role);

        userEntity.setCredential(credentialsEntity);
        UserEntity savedUserEntity = new UserEntity(1, credentialsEntity, "John", "+37368521164", new RoleEntity(2, "Admin"));

        when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));
        when(roleRepository.findRoleEntityByName("Admin")).thenReturn(Optional.of(new RoleEntity(2, "Admin")));
        when(userRepository.save(userEntity)).thenReturn(savedUserEntity);
        when(daoMapper.map(savedUserEntity)).thenReturn(user);
        when(credentialRepository.findById(1)).thenReturn(Optional.of(credentialsEntity));
        doNothing().when(userRoleChangeEmailListener).handleUserRoleChangeEvent(anyString());

        userService.updateUserRole(changeRoleRequest);

        verify(userRepository).findById(1);
        verify(roleRepository).findRoleEntityByName("Admin");
        verify(credentialRepository).findById(1);
        verify(userRoleChangeEmailListener).handleUserRoleChangeEvent("test@example.com");
    }

    @Test
    void updateUserRole_WhenCredentialsNotFound_ShouldThrowEntityNotFoundException() {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(1, "Admin");
        RoleEntity roleEntity = new RoleEntity(1, "User");
        UserEntity userEntity = new UserEntity(1, null, "John", "+37368521164", roleEntity);
        CredentialsEntity credentialsEntity = new CredentialsEntity(1, userEntity, "test@example.com", "Password");
        Role role = new Role("Admin");
        User user = new User(1, "John", "+37368521164", role);

        userEntity.setCredential(credentialsEntity);
        UserEntity savedUserEntity = new UserEntity(1, credentialsEntity, "John", "+37368521164", new RoleEntity(2, "Admin"));

        when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));
        when(roleRepository.findRoleEntityByName("Admin")).thenReturn(Optional.of(new RoleEntity(2, "Admin")));
        when(userRepository.save(userEntity)).thenReturn(savedUserEntity);
        when(daoMapper.map(savedUserEntity)).thenReturn(user);
        when(credentialRepository.findById(userEntity.getId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserRole(changeRoleRequest));

        verify(userRepository).findById(1);
        verify(credentialRepository).findById(userEntity.getId());
        verifyNoMoreInteractions(userRoleChangeEmailListener);
    }

}