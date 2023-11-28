package com.endava.internship.infrastructure.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.endava.internship.dao.entity.ParkingLevelEntity;
import com.endava.internship.dao.entity.ParkingLotEntity;
import com.endava.internship.dao.entity.ParkingSpotEntity;
import com.endava.internship.dao.entity.WorkingTimeEntity;
import com.endava.internship.dao.repository.ParkingLevelRepository;
import com.endava.internship.dao.repository.ParkingLotRepository;
import com.endava.internship.dao.repository.ParkingSpotRepository;
import com.endava.internship.dao.repository.WorkingTimeRepository;
import com.endava.internship.infrastructure.domain.ParkingLevel;
import com.endava.internship.infrastructure.domain.ParkingLot;
import com.endava.internship.infrastructure.domain.ParkingSpot;
import com.endava.internship.infrastructure.domain.WorkingTime;
import com.endava.internship.infrastructure.mapper.DaoMapper;
import com.endava.internship.infrastructure.mapper.DtoMapper;
import com.endava.internship.infrastructure.service.api.ParkingLotService;
import com.endava.internship.web.dto.CreateParkingLotResponse;
import com.endava.internship.web.dto.ParkingLevelDto;
import com.endava.internship.web.dto.WorkingTimeDto;
import com.endava.internship.web.request.CreateParkingLotRequest;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParkingLotServiceImpl implements ParkingLotService {
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingLevelRepository parkingLevelRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final WorkingTimeRepository workingTimeRepository;
    private final DaoMapper daoMapper;
    private final DtoMapper dtoMapper;

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
}