package com.endava.internship.infrastructure.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
public class EntityLinkException extends RuntimeException {
    public EntityLinkException(String message) {
        super(message);
    }
}