package com.endava.internship.infrastructure.service.api;

import com.endava.internship.web.dto.CreateParkingLotResponse;
import com.endava.internship.web.dto.ParkingLotDetailsDto;
import com.endava.internship.web.dto.UserToParkingLotDto;
import com.endava.internship.web.request.CreateParkingLotRequest;
import com.endava.internship.web.request.UpdateParkLotLinkRequest;

import java.util.List;

public interface ParkingLotService {

    CreateParkingLotResponse createParkingLot(CreateParkingLotRequest createParkingLotRequest);

    List<ParkingLotDetailsDto> getAllParkingLots();

    UserToParkingLotDto linkUserToParkingLot(UpdateParkLotLinkRequest updateParkLotLinkRequest);
}