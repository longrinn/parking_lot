package com.endava.internship.infrastructure.service.api;

import org.springframework.security.core.userdetails.UserDetails;

import com.endava.internship.web.dto.ParkingSpotDto;
import com.endava.internship.web.dto.ResponseDto;
import com.endava.internship.web.request.SpotOccupancyRequest;
import com.endava.internship.web.request.UpdateParkingSpotRequest;

public interface ParkingSpotService {

    ResponseDto deleteSpotUserLinkage(Integer parkingSpotId);

    ResponseDto editParkingSpot(Integer spotId, UpdateParkingSpotRequest updateStateInParkingSpotRequest);

    ParkingSpotDto occupyParkingSpot(Integer spotId, SpotOccupancyRequest spotOccupancyRequest, UserDetails userDetails);
}