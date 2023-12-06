package com.endava.internship.web.dto;

import java.time.LocalTime;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ParkingLotDetailsDto {

    private final Integer id;
    private final String name;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Set<WorkingTimeDto> workingTimesDto;
    private final boolean state;
    private final long totalSpots;
    private final long unavailableParkingSpots;
}