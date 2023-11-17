package com.endava.internship.infrastructure.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Credentials {

    private String email;
    private String password;
}