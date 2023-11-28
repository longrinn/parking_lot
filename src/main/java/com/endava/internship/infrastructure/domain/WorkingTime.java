package com.endava.internship.infrastructure.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Builder
@Setter
public class WorkingTime {

    private ParkingLot parkingLot;

    private String nameDay;
}
