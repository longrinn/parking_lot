package com.endava.internship.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SpotOccupancyRequest {
    @Email(message = "Pattern of the email is wrong")
    @NotBlank(message = "User email cannot be null")
    @Schema(example = "custom_email@example.com")
    private String userEmail;
}
