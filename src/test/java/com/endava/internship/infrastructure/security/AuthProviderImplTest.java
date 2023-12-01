package com.endava.internship.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthProviderImplTest {

    private final String email = "test@example.com";
    private final String correctPassword = "correctPassword";
    private final String wrongPassword = "wrongPassword";
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthProviderImpl authProvider;

    private Authentication createMockAuthentication(String email, String password) {
        final Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        when(authentication.getCredentials()).thenReturn(password);
        return authentication;
    }

    @Test
    public void authenticateWithValidCredentials() {
        final UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getPassword()).thenReturn(correctPassword);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(mockUserDetails);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        final Authentication authentication = createMockAuthentication(email, correctPassword);
        final Authentication result = authProvider.authenticate(authentication);

        assertNotNull(result);
    }

    @Test
    public void authenticateWithNonExistingUser() {
        when(userDetailsService.loadUserByUsername(email)).thenThrow(new UsernameNotFoundException("User not found"));

        final Authentication authentication = createMockAuthentication(email, wrongPassword);

        assertThrows(UsernameNotFoundException.class, () -> authProvider.authenticate(authentication));
    }

    @Test
    public void authenticateWithBadCredentials() {
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getPassword()).thenReturn(correctPassword);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(mockUserDetails);

        final Authentication authentication = createMockAuthentication(email, wrongPassword);

        assertThrows(BadCredentialsException.class, () -> authProvider.authenticate(authentication));
    }

    @Test
    void supports_UsernamePasswordAuthenticationToken_ReturnsTrue() {
        AuthProviderImpl authProvider = new AuthProviderImpl(null, null);
        Class<?> authenticationClass = UsernamePasswordAuthenticationToken.class;

        boolean result = authProvider.supports(authenticationClass);

        assertTrue(result);
    }

    @Test
    void supports_OtherAuthenticationToken_ReturnsFalse() {
        AuthProviderImpl authProvider = new AuthProviderImpl(null, null);
        Class<?> authenticationClass = AbstractAuthenticationToken.class;

        boolean result = authProvider.supports(authenticationClass);

        assertFalse(result);
    }

}