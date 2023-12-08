package com.endava.internship.infrastructure.exception;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleException(Exception ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(LocalDate.now(), ex.getMessage(),
                request.getDescription(false));

        return ResponseEntity.status(BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDetails> handleRuntimeException(RuntimeException ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(LocalDate.now(), ex.getMessage(),
                request.getDescription(false));

        return ResponseEntity.status(BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(LocalDate.now(), ex.getMessage(),
                request.getDescription(false));

        return ResponseEntity.status(NOT_FOUND).body(errorDetails);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(LocalDate.now(), ex.getMessage(),
                request.getDescription(false));

        return ResponseEntity.status(NOT_FOUND).body(errorDetails);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorDetails> handleSQLException(SQLException ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(LocalDate.now(), ex.getLocalizedMessage(),
                request.getDescription(false));

        return ResponseEntity.status(BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationExceptionResponse>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ValidationExceptionResponse> errors = ex.getBindingResult()
                .getAllErrors().stream()
                .map(error -> new ValidationExceptionResponse(((FieldError) error).getField(), error.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorDetails> handleEntityExistsException(EntityExistsException ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(LocalDate.now(), ex.getMessage(),
                request.getDescription(false));
        return ResponseEntity.status(CONFLICT).body(errorDetails);
    }

    @ExceptionHandler(EntityAlreadyLinkedException.class)
    public ResponseEntity<ErrorDetails> handleAlreadyLinkedEntitiesException(EntityAlreadyLinkedException ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(LocalDate.now(), ex.getMessage(),
                request.getDescription(false));

        return ResponseEntity.status(BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(EntityAreNotLinkedException.class)
    public ResponseEntity<ErrorDetails> handleNotLinkedEntitiesException(EntityAreNotLinkedException ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(LocalDate.now(), ex.getMessage(),
                request.getDescription(false));

        return ResponseEntity.status(BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(InvalidRequestParameterException.class)
    public ResponseEntity<ErrorDetails> handleInvalidRequestParameterException(InvalidRequestParameterException ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(LocalDate.now(), ex.getMessage(),
                request.getDescription(false));

        return ResponseEntity.status(BAD_REQUEST).body(errorDetails);
    }
}