package com.endava.internship.infrastructure.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ParkingSpot {

    private ParkingLevel parkingLevel;
    private boolean available;
    private String type;
    private User user;

}