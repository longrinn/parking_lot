package com.endava.internship.web.dto;

import java.time.LocalTime;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ParkingLotDto {

    @Schema(example = "1")
    private Integer id;
    @Schema(example = "Parking Lot")
    private String name;
    @Schema(example = "Parking's Address")
    private String address;
    @Schema(type = "String", pattern = "09:30")
    private LocalTime startTime;
    @Schema(type = "String", pattern = "20:30")
    private LocalTime endTime;
    private boolean state;
    private Set<WorkingTimeDto> workingTimes;
    private Set<ParkingLevelDetailsDto> parkingLevel;
}
