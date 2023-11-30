package com.endava.internship.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateParkLotLinkRequest {

    @Email(message = "Pattern of the email is wrong")
    @NotBlank(message = "User email cannot be null")
    String userEmail;

    @NotBlank(message = "Parking lot name cannot be null")
    String parkingLotName;
}