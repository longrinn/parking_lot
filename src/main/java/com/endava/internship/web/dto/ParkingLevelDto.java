package com.endava.internship.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParkingLevelDto {

    @Schema(example = "1")
    @NotNull(message = "Id cannot be null")
    private Integer id;

    @Schema(example = "1")
    @NotNull(message = "Floors cannot be null")
    private Integer floor;

    @Schema(example = "50")
    @NotNull(message = "Spots cannot be null")
    private Integer totalSpots;
}