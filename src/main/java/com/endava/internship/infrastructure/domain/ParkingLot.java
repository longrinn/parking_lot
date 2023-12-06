package com.endava.internship.infrastructure.domain;

import java.time.LocalTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ParkingLot {

    private Integer id;
    private String name;
    private String address;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean state;
    private Set<User> users;
}