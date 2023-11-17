package com.endava.internship.web.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidRegistrationRequest_noViolations() {
        RegistrationRequest request = new RegistrationRequest("Test user", "testUser@example.com", "Pass123!", "037312678");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenInvalidEmail_violationsFound() {
        RegistrationRequest request = new RegistrationRequest("John Doe", "invalidEmail", "Pass123!", "037312678");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenPassWithNoSymbol_violationsFound() {
        RegistrationRequest request = new RegistrationRequest("John Doe", "testUser@example.com", "Pass123", "037312678");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenPassWithNoDigit_violationsFound() {
        RegistrationRequest request = new RegistrationRequest("John Doe", "testUser@example.com", "Pass!", "037312678");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenPassWithNoUpperCase_violationsFound() {
        RegistrationRequest request = new RegistrationRequest("John Doe", "testUser@example.com", "pass1!", "037312678");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenPassWithNoLowerCase_violationsFound() {
        RegistrationRequest request = new RegistrationRequest("John Doe", "testUser@example.com", "PASS1!", "037312678");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenPassWith4Chars_violationsFound() {
        RegistrationRequest request = new RegistrationRequest("John Doe", "testUser@example.com", "Pa1!", "037312678");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenPassWith11Chars_violationsFound() {
        RegistrationRequest request = new RegistrationRequest("John Doe", "testUser@example.com", "PASS1!fsdgf", "037312678");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenPhoneHaveLessDigits_violationsFound() {
        RegistrationRequest request = new RegistrationRequest("John Doe", "testUser@example.com", "Pass123!", "0347865");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenPhoneHaveMoreDigits_violationsFound() {
        RegistrationRequest request = new RegistrationRequest("John Doe", "testUser@example.com", "Pass123!", "73781234845");
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }
}