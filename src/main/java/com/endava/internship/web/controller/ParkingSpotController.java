package com.endava.internship.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.endava.internship.infrastructure.service.ParkingSpotServiceImpl;
import com.endava.internship.web.dto.ResponseDto;
import com.endava.internship.web.request.UpdateParkingSpotRequest;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/spot")
@RequiredArgsConstructor
public class ParkingSpotController {

    private final ParkingSpotServiceImpl parkingSpotService;

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> editParkingSpot(@PathVariable Integer id, @RequestBody UpdateParkingSpotRequest request) {
        return ResponseEntity.status(OK).body(parkingSpotService.editParkingSpot(id, request));
    }
}