package com.endava.internship.infrastructure.security;

import com.endava.internship.infrastructure.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthProviderImplTest {

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private AuthProviderImpl authProvider;

    private final String email = "test@example.com";
    private final String correctPassword = "correctPassword";
    private final String wrongPassword = "wrongPassword";

    private Authentication createMockAuthentication(String email, String password) {
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        when(authentication.getCredentials()).thenReturn(password);
        return authentication;
    }

    @Test
    public void authenticateWithValidCredentials() {
        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        when(mockUserDetails.getPassword()).thenReturn(correctPassword);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(mockUserDetails);

        Authentication authentication = createMockAuthentication(email, correctPassword);
        Authentication result = authProvider.authenticate(authentication);

        assertNotNull(result);
    }

    @Test
    public void authenticateWithNonExistingUser() {
        when(userDetailsService.loadUserByUsername(email)).thenThrow(new UsernameNotFoundException("User not found"));

        Authentication authentication = createMockAuthentication(email, wrongPassword);

        assertThrows(UsernameNotFoundException.class, () -> authProvider.authenticate(authentication));
    }

    @Test
    public void authenticateWithBadCredentials() {
        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        when(mockUserDetails.getPassword()).thenReturn(correctPassword);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(mockUserDetails);

        Authentication authentication = createMockAuthentication(email, wrongPassword);

        assertThrows(BadCredentialsException.class, () -> authProvider.authenticate(authentication));
    }

}