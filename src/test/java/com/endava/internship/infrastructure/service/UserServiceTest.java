package com.endava.internship.infrastructure.service;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.endava.internship.dao.entity.CredentialsEntity;
import com.endava.internship.dao.entity.RoleEntity;
import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.dao.repository.RoleRepository;
import com.endava.internship.dao.repository.UserRepository;
import com.endava.internship.infrastructure.domain.Credentials;
import com.endava.internship.infrastructure.domain.Role;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.infrastructure.listeners.UserRoleChangeEmailListener;
import com.endava.internship.infrastructure.mapper.DaoMapper;
import com.endava.internship.infrastructure.mapper.DtoMapper;
import com.endava.internship.infrastructure.security.JwtUtils;
import com.endava.internship.infrastructure.security.UserDetailsImpl;
import com.endava.internship.web.dto.AuthenticationResponse;
import com.endava.internship.web.request.AuthenticationRequest;
import com.endava.internship.web.request.ChangeRoleRequest;
import com.endava.internship.web.request.RegistrationRequest;

import jakarta.persistence.EntityNotFoundException;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    JwtUtils jwtUtils;

    @InjectMocks
    private UserServiceImpl userService;



    @Test
    void registeredUserShouldBeAdded() {
        RegistrationRequest request = new RegistrationRequest("UserName", "user@mail.com", "User1!", "067860680");

        RoleEntity roleEntity = new RoleEntity(1, "User");
        UserEntity userEntity = new UserEntity(1, null, "UserName", "067860680", roleEntity, null, null);
        CredentialsEntity credentialsEntity = new CredentialsEntity(1, userEntity, "user@mail.com", "hashedPassword");

        when(bCryptPasswordEncoder.encode("User1!")).thenReturn("hashedPassword");
        when(daoMapper.map(any(Credentials.class))).thenReturn(credentialsEntity);
        when(daoMapper.map(any(User.class))).thenReturn(userEntity);
        when(roleRepository.findRoleEntityByName("User")).thenReturn(Optional.of(roleEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(jwtUtils.generateToken("user@mail.com")).thenReturn("mockedJwtToken");

        AuthenticationResponse response = userService.registration(request);

        verify(bCryptPasswordEncoder).encode("User1!");
        verify(daoMapper).map(any(Credentials.class));
        verify(daoMapper).map(any(User.class));
        verify(roleRepository).findRoleEntityByName("User");
        verify(userRepository).save(any(UserEntity.class));
        verify(jwtUtils).generateToken("user@mail.com");

        assertEquals("user@mail.com", response.getEmail());
        assertEquals("User", response.getRole());
        assertEquals("mockedJwtToken", response.getJwt());
    }

    @Test
    void register_WhenRoleIsNotPresent_ShouldThrowEntityNotFoundException() {
        RegistrationRequest request = new RegistrationRequest("UserName", "user@mail.com", "User1!", "067860680");

        when(roleRepository.findRoleEntityByName("User")).thenReturn(empty());

        assertThrows(EntityNotFoundException.class, () -> userService.registration(request));
    }

    @Test
    void authenticateUserShouldBeAuthenticated() {
        String expectedEmail = "user@mail.com";
        String expectedPassword = "User1!";
        String expectedName = "User";
        AuthenticationRequest request = new AuthenticationRequest(expectedEmail, expectedPassword);

        CredentialsEntity credentialsEntity = CredentialsEntity.builder()
                .email(expectedEmail)
                .password(expectedPassword)
                .build();
        RoleEntity roleEntity = new RoleEntity(1, expectedName);
        UserEntity userEntity = new UserEntity(1, credentialsEntity, expectedName, "067860680", roleEntity, null, null);
        UserDetails userDetails = new UserDetailsImpl(userEntity);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, expectedPassword, userDetails.getAuthorities());

        when(userRepository.findByCredential_Email(expectedEmail)).thenReturn(Optional.of(userEntity));
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateToken(expectedEmail)).thenReturn("mockedJwtToken");

        AuthenticationResponse response = userService.authentication(request);

        verify(userRepository).findByCredential_Email(expectedEmail);
        verify(authenticationManager).authenticate(any());
        verify(jwtUtils).generateToken(expectedEmail);

        assertEquals(expectedEmail, response.getEmail());
        assertEquals(expectedName, response.getRole());
        assertEquals("mockedJwtToken", response.getJwt());

    }

    @Test
    void registration_WhenUserIsNotFound_ShouldThrowEntityNotFoundException() {
        String email = "user@mail.com";
        AuthenticationRequest request = new AuthenticationRequest(email, "User1!");

        when(userRepository.findByCredential_Email(email)).thenReturn(empty());

        assertThrows(EntityNotFoundException.class, () -> userService.authentication(request));
    }

    @Test
    void updateUserRoleUserRoleShouldBeUpdated() {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest("email@.com", "Admin");
        RoleEntity roleEntity = new RoleEntity(1, "User");
        UserEntity userEntity = new UserEntity(1, null, "John", "+37368521164", roleEntity, null, null);
        User user = new User(1, "name", "+37326548958", new Role("Admin"), null);

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
        UserEntity user = new UserEntity(1, null, "John", "+37368521164", null, null, null);
        RoleEntity newRole = new RoleEntity(1, "Admin");
        user.setRole(newRole);

        when(userRepository.findByCredential_Email(changeRoleRequest.getEmail())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserRole(changeRoleRequest));
    }

    @Test
    void updateUserRole_WhenUserNotFound_ShouldThrowException() {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest("email@.mail", "Role");

        when(userRepository.findByCredential_Email(changeRoleRequest.getEmail())).thenReturn(empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserRole(changeRoleRequest));
    }

    @Test
    void updateUserRole_WhenRoleNotFound_ShouldThrowException() {
        final ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest("email@.mail", "Role");
        UserEntity user = new UserEntity(1, null, "John", "+37368521164", null, null, null);

        when(userRepository.findByCredential_Email(changeRoleRequest.getEmail())).thenReturn(Optional.of(user));
        when(roleRepository.findRoleEntityByName(changeRoleRequest.getNewRole())).thenReturn(empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUserRole(changeRoleRequest));
    }

    @Test
    void updateUserRole_WhenUserRoleIsUpdatedAndEmailSent_ShouldSucceed() {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest("test@example.com", "Admin");
        RoleEntity roleEntity = new RoleEntity(1, "User");
        UserEntity userEntity = new UserEntity(1, null, "John", "+37368521164", roleEntity, null, null);
        CredentialsEntity credentialsEntity = new CredentialsEntity(1, userEntity, "test@example.com", "Password");
        Role role = new Role("Admin");
        User user = new User(1, "John", "+37368521164", role, null);

        userEntity.setCredential(credentialsEntity);
        UserEntity savedUserEntity = new UserEntity(1, credentialsEntity, "John", "+37368521164", new RoleEntity(2, "Admin"), null, null);

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