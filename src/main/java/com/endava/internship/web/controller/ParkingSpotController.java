package com.endava.internship.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.endava.internship.infrastructure.service.api.ParkingSpotService;
import com.endava.internship.infrastructure.service.api.ParkingSpotService;
import com.endava.internship.web.dto.ParkingSpotDto;
import com.endava.internship.web.dto.ResponseDto;
import com.endava.internship.web.request.SpotOccupancyRequest;
import com.endava.internship.web.request.UpdateParkingSpotRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/spot")
@RequiredArgsConstructor
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> editParkingSpot(@PathVariable Integer id, @RequestBody UpdateParkingSpotRequest request) {
        return ResponseEntity.status(OK).body(parkingSpotService.editParkingSpot(id, request));
    }

    @Operation(
            description = "This endpoint is used to allow a user to occupy a specific parking spot"
    )
    @PostMapping("/{spotId}")
    public ResponseEntity<ParkingSpotDto> occupyParkingSpot(@PathVariable Integer spotId,
                                                            @RequestBody  @Valid SpotOccupancyRequest request,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(OK).body(parkingSpotService.occupyParkingSpot(spotId, request, userDetails));
    }

    @Operation(
            description = "This endpoint is used to remove the user from the occupied spot"
    )
    @DeleteMapping("/link/{id}")
    public ResponseEntity<ResponseDto> deleteLinkageUserSpot(@PathVariable Integer id) {
        return ResponseEntity.status(OK).body(parkingSpotService.deleteSpotUserLinkage(id));
    }
}