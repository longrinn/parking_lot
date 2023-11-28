package com.endava.internship.web.dto;

import java.time.LocalTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateParkingLotResponse {

    private String name;

    private String address;

    private LocalTime startTime;

    private LocalTime endTime;

    private boolean state;

    private Set<WorkingTimeDto> workingTimesDto;

    private Set<ParkingLevelDto> parkingLevelsDto;
}
