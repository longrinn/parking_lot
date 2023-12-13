package com.endava.internship.web.dto;

import java.time.LocalTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ParkingLotDto {

    private Integer id;
    private String name;
    private String address;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean state;
    private Set<WorkingTimeDto> workingTimes;
    private Set<ParkingLevelDetailsDto> parkingLevel;
}
