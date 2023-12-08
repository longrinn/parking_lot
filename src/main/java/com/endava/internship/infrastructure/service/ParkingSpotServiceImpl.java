package com.endava.internship.infrastructure.service;

import org.springframework.stereotype.Service;

import com.endava.internship.dao.entity.ParkingSpotEntity;
import com.endava.internship.dao.repository.ParkingSpotRepository;
import com.endava.internship.infrastructure.domain.ParkingSpot;
import com.endava.internship.infrastructure.exception.InvalidRequestParameterException;
import com.endava.internship.infrastructure.mapper.DaoMapper;
import com.endava.internship.infrastructure.service.api.ParkingSpotService;
import com.endava.internship.web.dto.ResponseDto;
import com.endava.internship.web.request.UpdateParkingSpotRequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import static com.endava.internship.infrastructure.util.ParkingLotConstants.SPOT_NOT_FOUND;
import static com.endava.internship.infrastructure.util.ParkingLotConstants.SPOT_UNAVAILABLE;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ParkingSpotServiceImpl implements ParkingSpotService {

    private static final String TEMPORARY_CLOSED = "Temporary closed";

    private final ParkingSpotRepository parkingSpotRepository;

    private final DaoMapper daoMapper;

    @Override
    public ResponseDto editParkingSpot(Integer spotId, UpdateParkingSpotRequest spotToUpdate) {
        final ParkingSpotEntity parkingSpotEntity = parkingSpotRepository
                .findById(spotId)
                .orElseThrow(() -> new EntityNotFoundException(SPOT_NOT_FOUND));

        final ParkingSpot parkingSpotUpdated = daoMapper.map(parkingSpotEntity);

        if (!parkingSpotUpdated.isAvailable()) {
            throw new InvalidRequestParameterException(SPOT_UNAVAILABLE);
        }

        if (nonNull(spotToUpdate.getType())) parkingSpotEntity.setType(spotToUpdate.getType());
        if (nonNull(spotToUpdate.getType()) &&
                spotToUpdate.getType().equals(TEMPORARY_CLOSED)) parkingSpotEntity.setAvailable(false);
        final ParkingSpot parkingSpot = daoMapper.map(parkingSpotEntity);
        parkingSpotRepository.save(parkingSpotEntity);

        return new ResponseDto(parkingSpot.getType());
    }
}