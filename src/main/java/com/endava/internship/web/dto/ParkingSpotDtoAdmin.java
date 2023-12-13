package com.endava.internship.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@AllArgsConstructor
public class ParkingSpotDtoAdmin {

    private Integer id;
    private String name;
    private boolean available;
    private String type;
    @JsonInclude(NON_NULL)
    private Integer userId;
    @JsonInclude(NON_NULL)
    private String userPhone;
}
