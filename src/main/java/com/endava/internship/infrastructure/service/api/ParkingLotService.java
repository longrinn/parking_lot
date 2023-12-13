package com.endava.internship.infrastructure.service.api;

import java.util.List;

import com.endava.internship.web.dto.ParkingLotDetailsDto;
import com.endava.internship.web.dto.ParkingLotDto;
import com.endava.internship.web.dto.ResponseDto;
import com.endava.internship.web.dto.UserToParkingLotDto;
import com.endava.internship.web.request.CreateParkingLotRequest;
import com.endava.internship.web.request.GetSpecificParkingLotRequest;
import com.endava.internship.web.request.UpdateParkLotLinkRequest;

public interface ParkingLotService {

    ResponseDto createParkingLot(CreateParkingLotRequest createParkingLotRequest);

    List<ParkingLotDetailsDto> getAllParkingLots();

    UserToParkingLotDto linkUserToParkingLot(UpdateParkLotLinkRequest updateParkLotLinkRequest);

    ResponseDto unlinkUserFromParkingLot(UpdateParkLotLinkRequest updateParkLotLinkRequest);

    ParkingLotDto getSpecificParkingLot(GetSpecificParkingLotRequest request);

    ResponseDto deleteParkingLot(Integer parkingLotId);
}