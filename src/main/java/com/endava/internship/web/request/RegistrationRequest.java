package com.endava.internship.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistrationRequest {

    @NotBlank(message = "Name can not be blank")
    @Schema(example = "UserName")
    private String name;

    @NotBlank(message = "Email can not be blank")
    @Email(message = "Invalid email format")
    @Schema(example = "custom_email@example.com")
    private String email;

    @NotBlank(message = "Password can not be blank")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*()+.?/{}_><,;:|=]).{5,10}$",
            message = "Password should have at least one digit, one upper case, one symbol, and be 5-10 characters long"
    )
    @Schema(example = "Pass_123!")
    private String password;

    @NotBlank(message = "Phone number can not be blank")
    @Pattern(
            regexp = "^\\d{9}$",
            message = "Phone number should be consisted of 9 digits"
    )
    private String number;
}