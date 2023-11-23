package com.endava.internship.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.exceptions.JWTVerificationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JwtUtilTest {
    private final static JwtUtils JWT_UTIL = new JwtUtils("SECRET");
    private final static String EMAIL = "test@example.com";
    private static String VALID_TOKEN;
    private final String INVALID_TOKEN = "invalid token";

    @BeforeEach
    public void set_up() {

        VALID_TOKEN = JWT_UTIL.generateToken(EMAIL);
    }

    @Test
    public void testGenerateToken() {
        assertNotNull(VALID_TOKEN);
    }

    @Test
    public void testValidateTokenWithValidToken() {
        String retrievedEmail = JWT_UTIL.validateTokenAndRetrieveClaim(VALID_TOKEN);

        assertEquals(EMAIL, retrievedEmail);
    }

    @Test
    public void testValidateTokenWithInvalidToken() {
        assertThrows(JWTVerificationException.class, () -> JWT_UTIL.validateTokenAndRetrieveClaim(INVALID_TOKEN));
    }
}