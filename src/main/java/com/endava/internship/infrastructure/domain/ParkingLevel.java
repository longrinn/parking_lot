package com.endava.internship.infrastructure.domain;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ParkingLevel {

    private Integer id;
    private ParkingLot parkingLot;
    private Integer floor;
    private Integer totalSpots;
    private Set<ParkingSpot> parkingSpots;
}
