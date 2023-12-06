package com.endava.internship.web.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.endava.internship.infrastructure.service.api.ParkingLotService;
import com.endava.internship.web.dto.ParkingLotDetailsDto;
import com.endava.internship.web.dto.ParkingLotDto;
import com.endava.internship.web.dto.ResponseDto;
import com.endava.internship.web.dto.UserToParkingLotDto;
import com.endava.internship.web.request.GetSpecificParkingLotRequest;
import com.endava.internship.web.request.ParkingLotRequest;
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
    public ResponseEntity<ResponseDto> createParkingLot(@RequestBody @Valid ParkingLotRequest parkingLotRequest) {

        return ResponseEntity.status(CREATED).body(parkingLotService.createParkingLot(parkingLotRequest));
    }

    @Operation(
            description = "This endpoint is used to update a parking lot"
    )
    @PutMapping("/parking-lots/{id}")
    public ResponseEntity<ResponseDto> updateParkingLot(@PathVariable Integer id, @RequestBody @Valid ParkingLotRequest parkingLotRequest) {
        return ResponseEntity.status(OK).body(parkingLotService.updateParkingLot(id, parkingLotRequest));
    }

    @Operation(
            description = "This endpoint is used to delete a parking lot"
    )
    @DeleteMapping("/parking-lot/{id}")
    public ResponseEntity<ResponseDto> deleteParkingLot(@PathVariable Integer id) {
        return ResponseEntity.status(OK).body(parkingLotService.deleteParkingLot(id));
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
    public ResponseEntity<UserToParkingLotDto> linkUserToParkLot(@RequestBody @Valid UpdateParkLotLinkRequest request) {
        return ResponseEntity.status(OK).body(parkingLotService.linkUserToParkingLot(request));
    }

    @Operation(
            description = "This endpoint is used to remove access from a user to a private parking lot"
    )
    @DeleteMapping("/link-park-lot")
    public ResponseEntity<ResponseDto> unlinkUserFromParkLot(@RequestBody @Valid UpdateParkLotLinkRequest request) {
        return ResponseEntity.status(OK).body(parkingLotService.unlinkUserFromParkingLot(request));
    }

    @Operation(
            description = "This endpoint is used to get a specific parking lot"
    )
    @GetMapping("/parking-lot")
    public ResponseEntity<ParkingLotDto> getSpecificParkingLot(@RequestBody @Valid GetSpecificParkingLotRequest request) {
        return ResponseEntity.status(OK).body(parkingLotService.getSpecificParkingLot(request));
    }
}