package com.endava.internship.web.dto;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.endava.internship.web.request.AuthenticationRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidAuthenticationRequest_noViolations() {
        AuthenticationRequest request = new AuthenticationRequest("testUser@example.com", "Pass123!");
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenInvalidEmail_violationsFound() {
        AuthenticationRequest request = new AuthenticationRequest("invalidEmail", "Pass123!");
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenPassWithNoSymbol_violationsFound() {
        AuthenticationRequest request = new AuthenticationRequest("testUser@example.com", "Pass123");
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenPassWithNoDigit_violationsFound() {
        AuthenticationRequest request = new AuthenticationRequest("testUser@example.com", "Pass!");
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenPassWithNoUpperCase_violationsFound() {
        AuthenticationRequest request = new AuthenticationRequest("testUser@example.com", "pass1!");
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenPassWithNoLowerCase_violationsFound() {
        AuthenticationRequest request = new AuthenticationRequest("testUser@example.com", "PASS1!");
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenPassWith4Chars_violationsFound() {
        AuthenticationRequest request = new AuthenticationRequest("testUser@example.com", "Pa1!");
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenPassWith11Chars_violationsFound() {
        AuthenticationRequest request = new AuthenticationRequest("testUser@example.com", "PASS1!fsdgf");
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }
}