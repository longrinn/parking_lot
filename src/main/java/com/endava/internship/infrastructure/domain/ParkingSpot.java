package com.endava.internship.infrastructure.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
@Setter
public class ParkingSpot {

    private Integer id;
    private ParkingLevel parkingLevel;
    private boolean available;
    private String type;
    private String name;
    private User user;
}