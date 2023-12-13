package com.endava.internship.web.dto;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ParkingLevelDetailsDto {

    @Schema(example = "1")
    private Integer id;

    @Schema(example ="1")
    private Integer floor;

    @Schema(example = "100")
    private Integer totalSpots;

    private Set<ParkingSpotDtoAdmin> parkingSpots;
}
