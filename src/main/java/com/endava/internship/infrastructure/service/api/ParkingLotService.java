package com.endava.internship.infrastructure.service.api;

import com.endava.internship.web.dto.CreateParkingLotResponse;
import com.endava.internship.web.dto.ParkingLotDto;
import com.endava.internship.web.request.CreateParkingLotRequest;

public interface ParkingLotService {
    CreateParkingLotResponse createParkingLot (CreateParkingLotRequest  createParkingLotRequest);
}
