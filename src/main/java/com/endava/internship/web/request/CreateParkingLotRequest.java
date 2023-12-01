package com.endava.internship.web.request;

import java.time.LocalTime;
import java.util.Set;

import com.endava.internship.web.dto.ParkingLevelDto;
import com.endava.internship.web.dto.WorkingTimeDto;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateParkingLotRequest {

    @NotBlank
    @Schema(example = "Parking Lot")
    private String name;

    @NotBlank
    @Schema(example = "Parking Lot Address")
    private String address;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    @NotNull
    private boolean state;

    @NotNull
    private Set<WorkingTimeDto> workingTimesDto;

    @NotNull
    private Set<ParkingLevelDto> parkingLevelsDto;
}
