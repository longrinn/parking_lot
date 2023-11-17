package com.endava.internship.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthenticationResponse {

    private String email;
    private String role;
    private String jwt;
}