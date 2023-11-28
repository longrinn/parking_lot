package com.endava.internship.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.endava.internship.infrastructure.service.api.ParkingLotService;
import com.endava.internship.web.dto.CreateParkingLotResponse;
import com.endava.internship.web.request.CreateParkingLotRequest;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    @PostMapping("/parking-lot")
    public ResponseEntity<CreateParkingLotResponse> createParkingLot(@RequestBody CreateParkingLotRequest  createParkingLotRequest) {

        return ResponseEntity.status(CREATED).body(parkingLotService.createParkingLot(createParkingLotRequest));
    }

}
