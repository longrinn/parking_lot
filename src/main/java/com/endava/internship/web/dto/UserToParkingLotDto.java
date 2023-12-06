package com.endava.internship.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserToParkingLotDto {
    @Schema(example = "custom_email@example.com")
    private String userEmail;
    @Schema(example = "user_name")
    private String userName;
    @Schema(example = "Parking Lot")
    private String parkingLotName;
    @Schema(example = "Parking's Address")
    private String parkingLotAddress;
}