package com.endava.internship.infrastructure.service;

import com.endava.internship.dao.entity.CredentialsEntity;
import com.endava.internship.dao.entity.RoleEntity;
import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.dao.repository.RoleRepository;
import com.endava.internship.dao.repository.UserRepository;
import com.endava.internship.infrastructure.domain.Role;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.infrastructure.listeners.UserRoleChangeEmailListener;
import com.endava.internship.infrastructure.mapper.DaoMapper;
import com.endava.internship.infrastructure.mapper.DtoMapper;
import com.endava.internship.web.request.ChangeRoleRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    UserRoleChangeEmailListener userRoleChangeEmailListener;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void updateUserRoleUserRoleShouldBeUpdated() {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest("email@.com", "Admin");
        RoleEntity roleEntity = new RoleEntity(1, "User");
        UserEntity userEntity = new UserEntity(1, null, "John", "+37368521164", roleEntity);
        User user = new User(1, "name", "+37326548958", new Role("Admin"));

        when(userRepository.findByCredential_Email("email@.com")).thenReturn(Optional.of(userEntity));
        when(roleRepository.findRoleEntityByName("Admin")).thenReturn(Optional.of(new RoleEntity(2, "Admin")));
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(daoMapper.map(userEntity)).thenReturn(user);

        userService.updateUserRole(changeRoleRequest);

        verify(userRepository).findByCredential_Email("email@.com");
        verify(roleRepository).findRoleEntityByName("Admin");

        ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userEntityCaptor.capture());

        UserEntity capturedUserEntity = userEntityCaptor.getValue();
        assertNotNull(capturedUserEntity);
        assertEquals("Admin", capturedUserEntity.getRole().getName());
    }

    @Test
    void updateUserRole_WhenUserEmailIsNull_ShouldThrowException() {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(null, "Role");
        UserEntity user = new UserEntity(1, null, "John", "+37368521164", null);
        RoleEntity newRole = new RoleEntity(1, "Admin");
        user.setRole(newRole);

        when(userRepository.findByCredential_Email(changeRoleRequest.getEmail())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserRole(changeRoleRequest));
    }

    @Test
    void updateUserRole_WhenUserNotFound_ShouldThrowException() {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest("email@.mail", "Role");
        UserEntity user = new UserEntity(1, null, "John", "+37368521164", null);

        when(userRepository.findByCredential_Email(changeRoleRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserRole(changeRoleRequest));
    }

    @Test
    void updateUserRole_WhenRoleNotFound_ShouldThrowException() {
        final ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest("email@.mail", "Role");
        UserEntity user = new UserEntity(1, null, "John", "+37368521164", null);
        RoleEntity newRole1 = new RoleEntity(1, "Admin");
        RoleEntity newRole2 = new RoleEntity(2, "User");

        when(userRepository.findByCredential_Email(changeRoleRequest.getEmail())).thenReturn(Optional.of(user));
        when(roleRepository.findRoleEntityByName(changeRoleRequest.getNewRole())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserRole(changeRoleRequest));
    }

    @Test
    void updateUserRole_WhenUserRoleIsUpdatedAndEmailSent_ShouldSucceed() {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest("test@example.com", "Admin");
        RoleEntity roleEntity = new RoleEntity(1, "User");
        UserEntity userEntity = new UserEntity(1, null, "John", "+37368521164", roleEntity);
        CredentialsEntity credentialsEntity = new CredentialsEntity(1, userEntity, "test@example.com", "Password");
        Role role = new Role("Admin");
        User user = new User(1, "John", "+37368521164", role);

        userEntity.setCredential(credentialsEntity);
        UserEntity savedUserEntity = new UserEntity(1, credentialsEntity, "John", "+37368521164", new RoleEntity(2, "Admin"));

        when(userRepository.findByCredential_Email("test@example.com")).thenReturn(Optional.of(userEntity));
        when(roleRepository.findRoleEntityByName("Admin")).thenReturn(Optional.of(new RoleEntity(2, "Admin")));
        when(userRepository.save(userEntity)).thenReturn(savedUserEntity);
        when(daoMapper.map(savedUserEntity)).thenReturn(user);
        doNothing().when(userRoleChangeEmailListener).handleUserRoleChangeEvent(anyString());

        userService.updateUserRole(changeRoleRequest);

        verify(userRepository).findByCredential_Email("test@example.com");
        verify(roleRepository).findRoleEntityByName("Admin");
        verify(userRoleChangeEmailListener).handleUserRoleChangeEvent("test@example.com");
    }
}