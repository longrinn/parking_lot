package com.endava.internship.infrastructure.service.api;

import com.endava.internship.web.dto.ResponseDto;
import com.endava.internship.web.request.UpdateParkingSpotRequest;

public interface ParkingSpotService {

    ResponseDto editParkingSpot(Integer spotId, UpdateParkingSpotRequest updateStateInParkingSpotRequest);
}