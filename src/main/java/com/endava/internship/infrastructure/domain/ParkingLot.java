package com.endava.internship.infrastructure.domain;

import java.time.LocalTime;
import java.util.Set;

import com.endava.internship.dao.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ParkingLot {

    private Integer id;
    private String name;
    private String address;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean state;
    private Set<WorkingTime> workingTimes;
    private Set<ParkingLevel> parkingLevels;
    private Set<UserEntity> users;
}