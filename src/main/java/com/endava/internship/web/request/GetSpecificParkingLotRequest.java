package com.endava.internship.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetSpecificParkingLotRequest {

    @Email(message = "Pattern of the email is wrong")
    @NotBlank(message = "User email cannot be null")
    @Schema(example = "custom_email@example.com")
    private String userEmail;

    @NotNull(message = "Parking lot id cannot be null")
    @Schema(example = "1")
    private Integer parkingLotId;
}
