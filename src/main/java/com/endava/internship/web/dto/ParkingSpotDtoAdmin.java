package com.endava.internship.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@AllArgsConstructor
public class ParkingSpotDtoAdmin {

    @Schema(example = "1")
    private Integer id;
    @Schema(example = "A-001")
    private String name;
    private boolean available;
    @Schema(example = "Regular")
    private String type;
    @Schema(example = "1")
    @JsonInclude(NON_NULL)
    private Integer userId;
    @Schema(example = "069374679")
    @JsonInclude(NON_NULL)
    private String userPhone;
}
