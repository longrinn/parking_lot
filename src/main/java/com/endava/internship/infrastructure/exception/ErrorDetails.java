package com.endava.internship.infrastructure.exception;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class ErrorDetails extends RuntimeException {

    private final LocalDate timeStamp;
    private final String message;
    private final String description;
}