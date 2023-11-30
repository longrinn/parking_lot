package com.endava.internship.infrastructure.service.api;

import com.endava.internship.web.dto.CreateParkingLotResponse;
import com.endava.internship.web.dto.UserToParkingLotDto;
import com.endava.internship.web.request.CreateParkingLotRequest;
import com.endava.internship.web.request.UpdateParkLotLinkRequest;

public interface ParkingLotService {
    CreateParkingLotResponse createParkingLot(CreateParkingLotRequest createParkingLotRequest);

    UserToParkingLotDto linkUserToParkingLot(UpdateParkLotLinkRequest updateParkLotLinkRequest);
}