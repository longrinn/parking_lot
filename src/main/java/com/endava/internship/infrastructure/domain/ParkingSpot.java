package com.endava.internship.infrastructure.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParkingSpot {

    private ParkingLevel parkingLevel;

    private Boolean state;

    private String type;

    private User user;

}
