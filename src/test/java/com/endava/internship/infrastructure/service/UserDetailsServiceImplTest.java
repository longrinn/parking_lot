package com.endava.internship.infrastructure.service;

import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.dao.repository.UserRepository;
import com.endava.internship.infrastructure.security.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static com.endava.internship.utils.TestUtils.EXISTING_USER_EMAIL;
import static com.endava.internship.utils.TestUtils.NON_EXISTENT_USER_EMAIL;
import static com.endava.internship.utils.TestUtils.PHONE_NUMBER;
import static com.endava.internship.utils.TestUtils.ROLE_USER;
import static com.endava.internship.utils.TestUtils.getUserEmail;
import static com.endava.internship.utils.TestUtils.getUserPhone;
import static com.endava.internship.utils.TestUtils.getUserRole;
import static com.endava.internship.utils.TestUtils.setupExistingUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Positive loadUserByUsername test")
    void testLoadUserByUsernameMethod() {
        UserEntity userEntity = setupExistingUser();

        when(userRepository.findByCredential_Email(anyString())).thenReturn(Optional.of(userEntity));

        UserDetails userDetails = userDetailsService.loadUserByUsername(EXISTING_USER_EMAIL);

        assertEquals(EXISTING_USER_EMAIL, getUserEmail(userDetails));
        assertEquals(ROLE_USER, getUserRole(userDetails));
        assertEquals(PHONE_NUMBER, getUserPhone(userDetails));
    }

    @Test
    @DisplayName("Negative loadUserByUsername test")
    void testLoadUserByUsernameMethodThrowsUsernameNotFoundException() {
        when(userRepository.findByCredential_Email(NON_EXISTENT_USER_EMAIL)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(NON_EXISTENT_USER_EMAIL));
    }
}
