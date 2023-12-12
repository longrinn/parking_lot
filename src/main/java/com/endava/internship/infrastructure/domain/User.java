package com.endava.internship.infrastructure.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
@Setter
public class User {

    private Integer id;
    private String name;
    private String phone;
    private Role role;
    private ParkingSpot parkingSpot;
}
