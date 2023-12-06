package com.endava.internship.infrastructure.exception;

import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class CustomExceptionHandlerTest {

    private static final String EXCEPTION_MESSAGE = "Exception message";
    private static final String EXCEPTION_DESCRIPTION = "Mocked description";

    private static final CustomExceptionHandler CUSTOM_EXCEPTION_HANDLER = new CustomExceptionHandler();
    private static final WebRequest WEB_REQUEST = mock(WebRequest.class);
    private static final int INVOCATIONS;
    private static final Stream<Arguments> ARGUMENTS_STREAM;

    static {
        when(WEB_REQUEST.getDescription(anyBoolean())).thenReturn(EXCEPTION_DESCRIPTION);
        final List<Arguments> arguments = List.of(
                Arguments.of(CUSTOM_EXCEPTION_HANDLER.handleException(new Exception(EXCEPTION_MESSAGE), WEB_REQUEST), BAD_REQUEST),
                Arguments.of(CUSTOM_EXCEPTION_HANDLER.handleRuntimeException(new RuntimeException(EXCEPTION_MESSAGE), WEB_REQUEST), BAD_REQUEST),
                Arguments.of(CUSTOM_EXCEPTION_HANDLER.handleSQLException(new SQLException(EXCEPTION_MESSAGE), WEB_REQUEST), BAD_REQUEST),
                Arguments.of(CUSTOM_EXCEPTION_HANDLER.handleEntityLinkException(new EntityLinkException(EXCEPTION_MESSAGE), WEB_REQUEST), BAD_REQUEST),
                Arguments.of(CUSTOM_EXCEPTION_HANDLER.handleInvalidRequestParameterException(new InvalidRequestParameterException(EXCEPTION_MESSAGE), WEB_REQUEST), BAD_REQUEST),
                Arguments.of(CUSTOM_EXCEPTION_HANDLER.handleDateTimeException(new DateTimeException(EXCEPTION_MESSAGE), WEB_REQUEST), BAD_REQUEST),
                Arguments.of(CUSTOM_EXCEPTION_HANDLER.handleEntityNotFoundException(new EntityNotFoundException(EXCEPTION_MESSAGE), WEB_REQUEST), NOT_FOUND),
                Arguments.of(CUSTOM_EXCEPTION_HANDLER.handleUsernameNotFoundException(new UsernameNotFoundException(EXCEPTION_MESSAGE), WEB_REQUEST), NOT_FOUND),
                Arguments.of(CUSTOM_EXCEPTION_HANDLER.handleEntityExistsException(new EntityExistsException(EXCEPTION_MESSAGE), WEB_REQUEST), CONFLICT));

        INVOCATIONS = arguments.size();
        ARGUMENTS_STREAM = arguments.stream();
    }

    private static Stream<Arguments> exceptionsSource() {
        return ARGUMENTS_STREAM;
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequestWithErrorMessage() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "error message");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<List<ValidationExceptionResponse>> responseEntity = CUSTOM_EXCEPTION_HANDLER.handleMethodArgumentNotValidException(exception);

        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        ValidationExceptionResponse validationExceptionResponse = responseEntity.getBody().get(0);
        assertEquals("fieldName", validationExceptionResponse.getFieldName());
        assertEquals("error message", validationExceptionResponse.getErrorMessage());
    }

    @ParameterizedTest
    @MethodSource("exceptionsSource")
    void handleException_ShouldReturnBadRequestWithErrorMessage(ResponseEntity<ErrorDetails> responseEntity, HttpStatus status) {
        assertThat(responseEntity.getStatusCode()).isEqualTo(status);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo(EXCEPTION_MESSAGE);
        assertThat(responseEntity.getBody().getTimeStamp()).isEqualTo(LocalDate.now());
        assertThat(responseEntity.getBody().getDescription()).isEqualTo(EXCEPTION_DESCRIPTION);

        verify(WEB_REQUEST, times(INVOCATIONS)).getDescription(anyBoolean());
    }
}