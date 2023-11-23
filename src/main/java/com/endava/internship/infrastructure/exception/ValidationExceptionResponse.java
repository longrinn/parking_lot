package com.endava.internship.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationExceptionResponse {

    private String fieldName;
    private String errorMessage;
}
