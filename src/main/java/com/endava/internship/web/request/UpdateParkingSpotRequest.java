package com.endava.internship.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateParkingSpotRequest {
    @Schema(example = "Parking spot type")
    @Size(min = 1, max = 30, message = "Parking spot type should have a size between 1 and 30 characters")
    private String type;
}