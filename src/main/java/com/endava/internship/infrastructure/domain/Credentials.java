package com.endava.internship.infrastructure.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Credentials {

    private String email;
    private String password;
}