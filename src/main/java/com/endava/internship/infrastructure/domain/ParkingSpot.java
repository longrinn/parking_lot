package com.endava.internship.infrastructure.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParkingSpot {

    private ParkingLevel parkingLevel;
    private boolean available;
    private String type;
    private User user;

}
