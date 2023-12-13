package com.endava.internship.infrastructure.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.endava.internship.dao.entity.ParkingLevelEntity;
import com.endava.internship.dao.entity.ParkingSpotEntity;
import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.dao.repository.ParkingSpotRepository;
import com.endava.internship.dao.repository.UserRepository;
import com.endava.internship.infrastructure.domain.ParkingLevel;
import com.endava.internship.infrastructure.domain.ParkingSpot;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.infrastructure.exception.EntityAreNotLinkedException;
import com.endava.internship.infrastructure.exception.InvalidRequestParameterException;
import com.endava.internship.infrastructure.mapper.DaoMapper;
import com.endava.internship.infrastructure.service.api.ParkingSpotService;
import com.endava.internship.web.dto.ParkingSpotDto;
import com.endava.internship.web.dto.ResponseDto;
import com.endava.internship.web.request.SpotOccupancyRequest;
import com.endava.internship.web.request.UpdateParkingSpotRequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import static com.endava.internship.infrastructure.util.ParkingLotConstants.PARKING_SPOT_NOT_FOUND_ERROR_MESSAGE;
import static com.endava.internship.infrastructure.util.ParkingLotConstants.SPOT_NOT_FOUND;
import static com.endava.internship.infrastructure.util.ParkingLotConstants.SPOT_UNAVAILABLE;
import static com.endava.internship.infrastructure.util.ParkingLotConstants.USER_EMAIL_NOT_FOUND_ERROR_MESSAGE;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ParkingSpotServiceImpl implements ParkingSpotService {

    private static final String TEMPORARY_CLOSED = "Temporary closed";
    private final ParkingSpotRepository parkingSpotRepository;
    private final DaoMapper daoMapper;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResponseDto deleteSpotUserLinkage(Integer id) {
        final ParkingSpotEntity parkingSpotEntity = parkingSpotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(PARKING_SPOT_NOT_FOUND_ERROR_MESSAGE, id)));

        if (parkingSpotEntity.isAvailable()) {
            throw new EntityAreNotLinkedException("No client uses this spot, it is not possible to delete a not existing linkage.");
        }

        parkingSpotRepository.deleteRelationUserSpotBySpotId(id);
        parkingSpotEntity.setAvailable(true);


        return new ResponseDto("Linkage successfully deleted!");
    }

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

    @Override
    @Transactional
    public ParkingSpotDto occupyParkingSpot(Integer spotId, SpotOccupancyRequest request, UserDetails userDetails) {
        final String userEmailFromJson = request.getUserEmail();
        final String userEmailFromJwt = userDetails.getUsername();
        if (!userEmailFromJson.equals(userEmailFromJwt)) {
            throw new IllegalArgumentException("User email from JSON does not match the email from JWT.");
        }
        ParkingSpotEntity parkingSpotEntity = parkingSpotRepository.findById(spotId)
                .orElseThrow(() -> new EntityNotFoundException("Parking spot not found"));
        ParkingSpot parkingSpot = daoMapper.map(parkingSpotEntity);
        ParkingLevelEntity parkingLevelEntity = parkingSpotEntity.getParkingLevel();
        ParkingLevel parkingLevel = daoMapper.map(parkingLevelEntity);

        if (!parkingSpot.isAvailable()) {
            throw new IllegalStateException("Parking spot is already occupied!");
        }
        UserEntity userEntity = userRepository.findByCredential_Email(userEmailFromJson)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(USER_EMAIL_NOT_FOUND_ERROR_MESSAGE, userEmailFromJson)));

        User user = daoMapper.map(userEntity);

        if (user.getParkingSpot() != null) {
            throw new IllegalStateException("User is already linked to a parking spot!");
        }

        parkingSpot.setAvailable(false);
        parkingSpot.setUser(user);
        parkingSpot.setParkingLevel(parkingLevel);
        ParkingSpotEntity parkingSpotEntityFinal = parkingSpotRepository.save(daoMapper.map(parkingSpot));
        user.setParkingSpot(daoMapper.map(parkingSpotEntityFinal));
        userRepository.save(daoMapper.map(user));

        String parkingSpotName = parkingSpotEntityFinal.getName();
        return new ParkingSpotDto(spotId, parkingSpotName,true,"New Type");
    }
}