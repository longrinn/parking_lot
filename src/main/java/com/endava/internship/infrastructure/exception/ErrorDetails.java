package com.endava.internship.infrastructure.exception;

import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorDetails {

    private final LocalDate timeStamp;
    private final String message;
    private final String description;
}
