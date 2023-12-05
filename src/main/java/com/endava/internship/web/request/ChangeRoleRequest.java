package com.endava.internship.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChangeRoleRequest {

    @NotBlank(message = "Email can not be blank")
    @Email(message = "Invalid email format")
    @Schema(example = "custom_email@example.com")
    private final String email;

    @NotBlank(message = "Role can not be blank")
    @Schema(example = "Admin")
    private final String role;
}