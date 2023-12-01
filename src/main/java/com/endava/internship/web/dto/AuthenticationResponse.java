package com.endava.internship.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthenticationResponse {

    @Schema(example = "custom_email@example.com")
    private String email;

    @Schema(example = "User")
    private String role;

    @Schema(example = "Custom_JWT")
    private String jwt;
}