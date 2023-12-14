package com.endava.internship.infrastructure.service.api;

import java.util.List;

import com.endava.internship.web.dto.ParkingLotDetailsDto;
import com.endava.internship.web.dto.ParkingLotDto;
import com.endava.internship.web.dto.ResponseDto;
import com.endava.internship.web.dto.UserToParkingLotDto;
import com.endava.internship.web.request.ParkingLotRequest;
import com.endava.internship.web.request.UpdateParkLotLinkRequest;

public interface ParkingLotService {

    ResponseDto createParkingLot(ParkingLotRequest createParkingLotRequest);

    ResponseDto updateParkingLot(Integer id, ParkingLotRequest parkingLotRequest);

    List<ParkingLotDetailsDto> getAllParkingLots();

    UserToParkingLotDto linkUserToParkingLot(UpdateParkLotLinkRequest updateParkLotLinkRequest);

    ResponseDto unlinkUserFromParkingLot(UpdateParkLotLinkRequest updateParkLotLinkRequest);

    ParkingLotDto getSpecificParkingLot(String userEmail, Integer parkingLotId);

    ResponseDto deleteParkingLot(Integer parkingLotId);
}