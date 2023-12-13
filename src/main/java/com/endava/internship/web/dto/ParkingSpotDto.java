package com.endava.internship.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ParkingSpotDto {

    private final Integer id;
    private final String name;
    private final boolean available;
    private final String type;
}
