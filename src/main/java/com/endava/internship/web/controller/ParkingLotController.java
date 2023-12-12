package com.endava.internship.web.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.endava.internship.infrastructure.service.api.ParkingLotService;
import com.endava.internship.web.dto.ParkingLotDetailsDto;
import com.endava.internship.web.dto.ResponseDto;
import com.endava.internship.web.dto.UserToParkingLotDto;
import com.endava.internship.web.request.CreateParkingLotRequest;
import com.endava.internship.web.request.UpdateParkLotLinkRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    @Operation(
            responses = @ApiResponse(
                    description = "Created",
                    responseCode = "201"
            ),
            description = "This endpoint is used to create a parking lot"
    )
    @PostMapping("/parking-lot")
    public ResponseEntity<ResponseDto> createParkingLot(@RequestBody @Valid CreateParkingLotRequest createParkingLotRequest) {

        return ResponseEntity.status(CREATED).body(parkingLotService.createParkingLot(createParkingLotRequest));
    }

    @Operation(
            description = "This endpoint is used to delete a parking lot"
    )
    @DeleteMapping("/parking-lot/{parkingLotId}")
    public ResponseEntity<ResponseDto> deleteParkingLot(@PathVariable Integer parkingLotId) {
        return ResponseEntity.status(OK).body(parkingLotService.deleteParkingLot(parkingLotId));
    }

    @Operation(
            description = "This endpoint is used to get all parking lots"
    )
    @GetMapping("/parking-lots")
    public ResponseEntity<List<ParkingLotDetailsDto>> getAllParkingLots() {
        return ResponseEntity.status(OK).body(parkingLotService.getAllParkingLots());
    }

    @Operation(
            description = "This endpoint is used to grant access to a user to a private parking lot"
    )
    @PostMapping("/link-park-lot")
    public ResponseEntity<UserToParkingLotDto> linkUserToParkLot(@RequestBody UpdateParkLotLinkRequest request) {
        return ResponseEntity.status(OK).body(parkingLotService.linkUserToParkingLot(request));
    }

    @Operation(
            description = "This endpoint is used to remove access from a user to a private parking lot"
    )
    @DeleteMapping("/link-park-lot")
    public ResponseEntity<ResponseDto> unlinkUserFromParkLot(@RequestBody UpdateParkLotLinkRequest request) {
        return ResponseEntity.status(OK).body(parkingLotService.unlinkUserFromParkingLot(request));
    }
}