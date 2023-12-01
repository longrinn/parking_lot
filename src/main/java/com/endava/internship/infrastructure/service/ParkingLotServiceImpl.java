package com.endava.internship.infrastructure.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.endava.internship.dao.entity.ParkingLevelEntity;
import com.endava.internship.dao.entity.ParkingLotEntity;
import com.endava.internship.dao.entity.ParkingSpotEntity;
import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.dao.entity.WorkingTimeEntity;
import com.endava.internship.dao.repository.ParkingLevelRepository;
import com.endava.internship.dao.repository.ParkingLotRepository;
import com.endava.internship.dao.repository.ParkingSpotRepository;
import com.endava.internship.dao.repository.UserRepository;
import com.endava.internship.dao.repository.WorkingTimeRepository;
import com.endava.internship.infrastructure.domain.ParkingLevel;
import com.endava.internship.infrastructure.domain.ParkingLot;
import com.endava.internship.infrastructure.domain.ParkingSpot;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.infrastructure.domain.WorkingTime;
import com.endava.internship.infrastructure.exception.EntityAlreadyLinkedException;
import com.endava.internship.infrastructure.exception.EntityAreNotLinkedException;
import com.endava.internship.infrastructure.listeners.UserLinkToParkLotListener;
import com.endava.internship.infrastructure.listeners.UserUnlinkFromParkingLotListener;
import com.endava.internship.infrastructure.mapper.DaoMapper;
import com.endava.internship.infrastructure.mapper.DtoMapper;
import com.endava.internship.infrastructure.service.api.ParkingLotService;
import com.endava.internship.web.dto.CreateParkingLotResponse;
import com.endava.internship.web.dto.ParkingLevelDto;
import com.endava.internship.web.dto.ParkingLotDetailsDto;
import com.endava.internship.web.dto.UnlinkUserDto;
import com.endava.internship.web.dto.UserToParkingLotDto;
import com.endava.internship.web.dto.WorkingTimeDto;
import com.endava.internship.web.request.CreateParkingLotRequest;
import com.endava.internship.web.request.UpdateParkLotLinkRequest;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import static com.endava.internship.infrastructure.util.ParkingLotConstants.ENTITIES_ALREADY_LINKED;
import static com.endava.internship.infrastructure.util.ParkingLotConstants.ENTITIES_NOT_LINKED;
import static com.endava.internship.infrastructure.util.ParkingLotConstants.PARKING_LOT_NAME_NOT_FOUND_ERROR_MESSAGE;
import static com.endava.internship.infrastructure.util.ParkingLotConstants.USER_EMAIL_NOT_FOUND_ERROR_MESSAGE;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class ParkingLotServiceImpl implements ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingLevelRepository parkingLevelRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final WorkingTimeRepository workingTimeRepository;
    private final UserRepository userRepository;
    private final UserLinkToParkLotListener userLinkToParkLotListener;
    private final DaoMapper daoMapper;
    private final DtoMapper dtoMapper;
    private final UserUnlinkFromParkingLotListener userUnlinkFromParkLotListener;

    @Override
    @Transactional
    public CreateParkingLotResponse createParkingLot(CreateParkingLotRequest createParkingLotRequest) {
        String parkingLotName = createParkingLotRequest.getName();

        if (!parkingLotRepository.existsByName(parkingLotName)) {
            // Create ParkingLot
            ParkingLot parkingLotToBeSaved = ParkingLot.builder()
                    .name(parkingLotName)
                    .address(createParkingLotRequest.getAddress())
                    .startTime(createParkingLotRequest.getStartTime())
                    .endTime(createParkingLotRequest.getEndTime())
                    .state(createParkingLotRequest.isState())
                    .build();
            // Save ParkingLot
            ParkingLotEntity parkingLotEntity = daoMapper.map(parkingLotToBeSaved);
            ParkingLotEntity ptEntity = parkingLotRepository.save(parkingLotEntity);
            ParkingLot parkingLot = daoMapper.map(ptEntity);

            // Create ParkingLevels
            int counterOfLevels = 0;
            Set<ParkingLevel> parkingLevels = new HashSet<>();
            for (ParkingLevelDto parkingLevelDto : createParkingLotRequest.getParkingLevelsDto()) {
                ParkingLevel parkingLevel = ParkingLevel.builder()
                        .parkingLot(parkingLot)
                        .floor(parkingLevelDto.getFloor())
                        .totalSpots(parkingLevelDto.getTotalSpots())
                        .build();
                // Save ParkingLevel
                ParkingLevelEntity parkingLevelEntity = daoMapper.map(parkingLevel);
                parkingLevelRepository.save(parkingLevelEntity);
                // FetchParkingLevelEntity which already contains the ID of the level, needed for ParkingSpotEntity
                List<ParkingLevelEntity> parkingLevelEntity1 = parkingLevelRepository.getByParkingLotId(parkingLot.getId());
                ParkingLevel fetchedParkingLevelWithID = daoMapper.map(parkingLevelEntity1.get(counterOfLevels));

                // Create ParkingSpots for this level
                Set<ParkingSpot> parkingSpots = new HashSet<>();
                for (int i = 1; i <= parkingLevelDto.getTotalSpots(); i++) {
                    ParkingSpot parkingSpot = ParkingSpot.builder()
                            .parkingLevel(fetchedParkingLevelWithID)
                            .user(null)
                            .type("Regular")
                            .state(false)
                            .build();
                    parkingSpots.add(parkingSpot);

                    ParkingSpotEntity parkingSpotEntity = daoMapper.map(parkingSpot);
                    parkingSpotRepository.save(parkingSpotEntity);
                }
                fetchedParkingLevelWithID.setParkingSpots(parkingSpots);
                parkingLevels.add(fetchedParkingLevelWithID);
                counterOfLevels++;
            }

            // Create and Save WorkingTimes
            Set<WorkingTime> workingTimes = new HashSet<>();
            for (WorkingTimeDto workingTimeDto : createParkingLotRequest.getWorkingTimesDto()) {
                WorkingTime workingTime = WorkingTime.builder()
                        .parkingLot(parkingLot)
                        .nameDay(workingTimeDto.getNameDay())
                        .build();
                workingTimes.add(workingTime);
                WorkingTimeEntity workingTimeEntity = daoMapper.map(workingTime);
                workingTimeRepository.save(workingTimeEntity);
            }
            // Save ParkingLot
            ParkingLotEntity parkingLotEntity1 = daoMapper.map(parkingLot);
            parkingLotRepository.save(parkingLotEntity1);

            // return response
            return new CreateParkingLotResponse(
                    parkingLot.getName(),
                    parkingLot.getAddress(),
                    parkingLot.getStartTime(),
                    parkingLot.getEndTime(),
                    parkingLot.isState(),
                    dtoMapper.mapWorkingTimes(workingTimes),
                    dtoMapper.mapParkingLevels(parkingLevels)
            );
        } else {
            throw new EntityExistsException("Parking Lot with the specified name already exists");
        }
    }

    @Override
    @Transactional
    public UserToParkingLotDto linkUserToParkingLot(UpdateParkLotLinkRequest updateParkLotLinkRequest) {

        final String parkingLotName = updateParkLotLinkRequest.getParkingLotName();
        final String userEmail = updateParkLotLinkRequest.getUserEmail();

        final UserEntity userEntity = userRepository.findByCredential_Email(userEmail)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(USER_EMAIL_NOT_FOUND_ERROR_MESSAGE, userEmail)));

        final ParkingLotEntity parkingLotEntity = parkingLotRepository.findByName(parkingLotName)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(PARKING_LOT_NAME_NOT_FOUND_ERROR_MESSAGE, parkingLotName)));

        if (userEntity.getParkingLots().contains(parkingLotEntity)) {
            throw new EntityAlreadyLinkedException(String.format(ENTITIES_ALREADY_LINKED));
        }

        userEntity.getParkingLots().add(parkingLotEntity);
        userRepository.save(userEntity);

        User userDomain = daoMapper.map(userEntity);
        ParkingLot parkingLotDomain = daoMapper.map(parkingLotEntity);

        final Set<ParkingLotEntity> userNewParkingLots = userEntity.getParkingLots();

        UserToParkingLotDto emailDetails = new UserToParkingLotDto(userEntity.getCredential().getEmail(), userEntity.getName(), parkingLotEntity.getName(), parkingLotEntity.getAddress());

        if (userNewParkingLots.contains(parkingLotEntity)) {
            userLinkToParkLotListener.handleUserLinkToParkLotEvent(emailDetails);
        }

        return dtoMapper.map(userDomain, parkingLotDomain, userEmail);
    }

    @Override
    @Transactional
    public List<ParkingLotDetailsDto> getAllParkingLots() {
        final List<ParkingLotDetailsDto> parkingLotDetailsDtos = new ArrayList<>();
        final List<ParkingLotEntity> allParkingLots = parkingLotRepository.findAll();
        final List<ParkingLot> parkingLots = allParkingLots.stream()
                .map(daoMapper::map)
                .toList();

        for (ParkingLot parkingLot : parkingLots) {
            final List<ParkingLevelEntity> parkingLevels = parkingLevelRepository.getByParkingLotId(parkingLot.getId());
            long unavailableSpots = parkingLevels.stream()
                    .map(ParkingLevelEntity::getParkingSpots)
                    .flatMap(Collection::stream)
                    .map(ParkingSpotEntity::isAvailable)
                    .filter(val -> !val)
                    .count();

            long totalSpots = parkingLevels.stream()
                    .mapToLong(ParkingLevelEntity::getTotalSpots)
                    .sum();

            final Set<WorkingTimeDto> workingTimeDtos = workingTimeRepository.findByParkingLot_Id(parkingLot.getId()).stream()
                    .map(daoMapper::map)
                    .map(dtoMapper::mapWorkingTimes)
                    .flatMap(Collection::stream)
                    .collect(toSet());

            ParkingLotDetailsDto response = new ParkingLotDetailsDto(parkingLot.getName(), parkingLot.getStartTime(), parkingLot.getEndTime(),
                    workingTimeDtos, parkingLot.isState(), totalSpots, unavailableSpots);
            parkingLotDetailsDtos.add(response);
        }
        return parkingLotDetailsDtos;
    }

    @Override
    @Transactional
    public UnlinkUserDto unlinkUserFromParkingLot(UpdateParkLotLinkRequest updateParkLotLinkRequest) {

        final String parkingLotName = updateParkLotLinkRequest.getParkingLotName();
        final String userEmail = updateParkLotLinkRequest.getUserEmail();

        final UserEntity userEntity = userRepository.findByCredential_Email(userEmail)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(USER_EMAIL_NOT_FOUND_ERROR_MESSAGE, userEmail)));

        final ParkingLotEntity parkingLotEntity = parkingLotRepository.findByName(parkingLotName)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(PARKING_LOT_NAME_NOT_FOUND_ERROR_MESSAGE, parkingLotName)));

        if (!userEntity.getParkingLots().contains(parkingLotEntity)) {
            throw new EntityAreNotLinkedException(String.format(ENTITIES_NOT_LINKED));
        }

        userEntity.getParkingLots().remove(parkingLotEntity);

        final User userDomain = daoMapper.map(userEntity);

        final Set<ParkingLotEntity> userNewParkingLots = userEntity.getParkingLots();

        final UserToParkingLotDto emailDetails = new UserToParkingLotDto(userEntity.getCredential().getEmail(), userEntity.getName(), parkingLotEntity.getName(), parkingLotEntity.getAddress());

        if (!userNewParkingLots.contains(parkingLotEntity)) {
            userUnlinkFromParkLotListener.handleUserUnlinkFromParkLotEvent(emailDetails);
        }

        return new UnlinkUserDto(userDomain.getName() + " has been unlinked");
    }
}