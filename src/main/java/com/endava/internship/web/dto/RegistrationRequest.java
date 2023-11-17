package com.endava.internship.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistrationRequest {

    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*()+.?/{}_><,;:|=]).{5,10}$",
            message = "Password should have at least one digit, one upper case, one symbol, and be 5-10 characters long"
    )
    private String password;

    @Pattern(
            regexp = "^\\d{9}$",
            message = "Phone number should be consisted of 9 digits"
    )
    private String number;
}