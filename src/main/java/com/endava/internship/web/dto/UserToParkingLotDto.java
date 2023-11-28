package com.endava.internship.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserToParkingLotDto {
    private String userEmail;
    private String userName;
    private String parkingLotName;
    private String parkingLotAddress;
}