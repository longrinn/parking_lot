package com.endava.internship.infrastructure.exception;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomExceptionHandlerTest {

    private CustomExceptionHandler customExceptionHandler;

    private WebRequest mockWebRequest;

    @BeforeEach
    public void setup() {
        customExceptionHandler = new CustomExceptionHandler();
        mockWebRequest = mock(WebRequest.class);

        when(mockWebRequest.getDescription(anyBoolean())).thenReturn("Mocked description");
    }

    @Test
    void handleException_ShouldReturnBadRequestWithErrorMessage() {
        Exception exception = new Exception("Exception message");

        ResponseEntity<ErrorDetails> responseEntity = customExceptionHandler.handleException(exception, mockWebRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Exception message", responseEntity.getBody().getMessage());
        assertEquals(LocalDate.now(), responseEntity.getBody().getTimeStamp());
        assertEquals("Mocked description", responseEntity.getBody().getDescription());
    }

    @Test
    void handleRuntimeException_ShouldReturnBadRequestWithErrorMessage() {
        RuntimeException exception = new RuntimeException("Exception message");

        ResponseEntity<ErrorDetails> responseEntity = customExceptionHandler.handleRuntimeException(exception, mockWebRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Exception message", responseEntity.getBody().getMessage());
        assertEquals(LocalDate.now(), responseEntity.getBody().getTimeStamp());
        assertEquals("Mocked description", responseEntity.getBody().getDescription());
    }

    @Test
    void handleUsernameNotFoundException_ShouldReturnNotFoundWithErrorMessage() {
        UsernameNotFoundException exception = new UsernameNotFoundException("Exception message");

        ResponseEntity<ErrorDetails> responseEntity = customExceptionHandler.handleUsernameNotFoundException(exception, mockWebRequest);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Exception message", responseEntity.getBody().getMessage());
        assertEquals(LocalDate.now(), responseEntity.getBody().getTimeStamp());
        assertEquals("Mocked description", responseEntity.getBody().getDescription());
    }

    @Test
    void handleEntityNotFoundException_ShouldReturnNotFoundWithErrorMessage() {
        EntityNotFoundException exception = new EntityNotFoundException("Exception message");

        ResponseEntity<ErrorDetails> responseEntity = customExceptionHandler.handleEntityNotFoundException(exception, mockWebRequest);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Exception message", responseEntity.getBody().getMessage());
        assertEquals(LocalDate.now(), responseEntity.getBody().getTimeStamp());
        assertEquals("Mocked description", responseEntity.getBody().getDescription());
    }

    @Test
    void handleSQLException_ShouldReturnBadRequestWithErrorMessage() {
        SQLException exception = new SQLException("Exception message");


        ResponseEntity<ErrorDetails> responseEntity = customExceptionHandler.handleSQLException(exception, mockWebRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Exception message", responseEntity.getBody().getMessage());
        assertEquals(LocalDate.now(), responseEntity.getBody().getTimeStamp());
        assertEquals("Mocked description", responseEntity.getBody().getDescription());
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequestWithErrorMessage() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "error message");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<List<ValidationExceptionResponse>> responseEntity = customExceptionHandler.handleMethodArgumentNotValidException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        ValidationExceptionResponse validationExceptionResponse = responseEntity.getBody().get(0);
        assertEquals("fieldName", validationExceptionResponse.getFieldName());
        assertEquals("error message", validationExceptionResponse.getErrorMessage());
    }

    @Test
    void handleEntityExistsException_ShouldReturnConflictWithErrorMessage() {
        EntityExistsException exception = new EntityExistsException("Exception message");

        ResponseEntity<ErrorDetails> responseEntity = customExceptionHandler.handleEntityExistsException(exception, mockWebRequest);

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Exception message", responseEntity.getBody().getMessage());
        assertEquals(LocalDate.now(), responseEntity.getBody().getTimeStamp());
        assertEquals("Mocked description", responseEntity.getBody().getDescription());
    }

    @Test
    void handleEntityAlreadyLinkedException_ShouldReturnBadRequestWithErrorMessage() {
        EntityAlreadyLinkedException exception = new EntityAlreadyLinkedException("Exception message");

        ResponseEntity<ErrorDetails> responseEntity = customExceptionHandler.handleAlreadyLinkedEntitiesException(exception, mockWebRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Exception message", responseEntity.getBody().getMessage());
        assertEquals(LocalDate.now(), responseEntity.getBody().getTimeStamp());
        assertEquals("Mocked description", responseEntity.getBody().getDescription());
    }

    @Test
    void handleEntityAreNotLinkedException_ShouldReturnBadRequestWithErrorMessage() {
        EntityAreNotLinkedException exception = new EntityAreNotLinkedException("Exception message");

        ResponseEntity<ErrorDetails> responseEntity = customExceptionHandler.handleNotLinkedEntitiesException(exception, mockWebRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Exception message", responseEntity.getBody().getMessage());
        assertEquals(LocalDate.now(), responseEntity.getBody().getTimeStamp());
        assertEquals("Mocked description", responseEntity.getBody().getDescription());
    }
}