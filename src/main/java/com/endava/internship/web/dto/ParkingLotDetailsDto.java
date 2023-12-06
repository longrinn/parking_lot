package com.endava.internship.web.dto;

import java.time.LocalTime;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ParkingLotDetailsDto {
    @Schema(example = "1")
    private final Integer id;
    @Schema(example = "Parking Lot")
    private final String name;
    @Schema(example = "Parking Address")
    private final String address;
    @Schema(type = "String", pattern = "07:45")
    private final LocalTime startTime;
    @Schema(type = "String", pattern = "23:15")
    private final LocalTime endTime;
    private final Set<WorkingTimeDto> workingTimesDto;
    private final boolean state;
    @Schema(example = "300")
    private final long totalSpots;
    @Schema(example = "125")
    private final long unavailableParkingSpots;
}