package com.endava.internship.web.request;

import java.time.LocalTime;
import java.util.Set;

import com.endava.internship.web.dto.ParkingLevelDto;
import com.endava.internship.web.dto.WorkingTimeDto;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateParkingLotRequest {

    @NotBlank(message = "Parking lot name cannot be blank")
    @Schema(example = "Parking Lot")
    private String name;

    @NotBlank(message = "Parking lot address cannot be blank")
    @Schema(example = "Parking Lot Address")
    private String address;

    @NotNull(message = "Start Time cannot be null")
    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "String", pattern = "09:30:00")
    private LocalTime startTime;

    @NotNull(message = "End Time cannot be null")
    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "String", pattern = "19:30:00")
    private LocalTime endTime;

    @NotNull(message = "State cannot be null")
    private boolean state;

    @NotEmpty(message = "Working time list cannot be empty")
    private Set<WorkingTimeDto> workingTimesDto;

    @NotEmpty(message = "Parking levels list cannot be empty")
    private Set<ParkingLevelDto> parkingLevelsDto;
}