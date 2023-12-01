package com.endava.internship.infrastructure.service.api;

import java.util.List;

import com.endava.internship.web.dto.CreateParkingLotResponse;
import com.endava.internship.web.dto.ParkingLotDetailsDto;
import com.endava.internship.web.dto.UnlinkUserDto;
import com.endava.internship.web.dto.UserToParkingLotDto;
import com.endava.internship.web.request.CreateParkingLotRequest;
import com.endava.internship.web.request.UpdateParkLotLinkRequest;

public interface ParkingLotService {

    CreateParkingLotResponse createParkingLot(CreateParkingLotRequest createParkingLotRequest);

    List<ParkingLotDetailsDto> getAllParkingLots();

    UserToParkingLotDto linkUserToParkingLot(UpdateParkLotLinkRequest updateParkLotLinkRequest);

    UnlinkUserDto unlinkUserFromParkingLot(UpdateParkLotLinkRequest updateParkLotLinkRequest);
}