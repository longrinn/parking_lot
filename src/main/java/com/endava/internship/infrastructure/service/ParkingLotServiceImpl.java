package com.endava.internship.infrastructure.service;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;
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
import com.endava.internship.dao.repository.RoleRepository;
import com.endava.internship.dao.repository.UserRepository;
import com.endava.internship.dao.repository.WorkingTimeRepository;
import com.endava.internship.infrastructure.domain.ParkingLevel;
import com.endava.internship.infrastructure.domain.ParkingLot;
import com.endava.internship.infrastructure.domain.ParkingSpot;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.infrastructure.domain.WorkingTime;
import com.endava.internship.infrastructure.exception.EntityLinkException;
import com.endava.internship.infrastructure.exception.InvalidRequestParameterException;
import com.endava.internship.infrastructure.listeners.UserLinkToParkLotListener;
import com.endava.internship.infrastructure.listeners.UserUnlinkFromParkingLotListener;
import com.endava.internship.infrastructure.mapper.DaoMapper;
import com.endava.internship.infrastructure.mapper.DtoMapper;
import com.endava.internship.infrastructure.service.api.ParkingLotService;
import com.endava.internship.web.dto.ParkingLevelDetailsDto;
import com.endava.internship.web.dto.ParkingLevelDto;
import com.endava.internship.web.dto.ParkingLotDetailsDto;
import com.endava.internship.web.dto.ParkingLotDto;
import com.endava.internship.web.dto.ParkingSpotDtoAdmin;
import com.endava.internship.web.dto.ResponseDto;
import com.endava.internship.web.dto.UserToParkingLotDto;
import com.endava.internship.web.dto.WorkingTimeDto;
import com.endava.internship.web.request.ParkingLotRequest;
import com.endava.internship.web.request.UpdateParkLotLinkRequest;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import static com.endava.internship.infrastructure.util.ParkingLotConstants.ENTITIES_ALREADY_LINKED;
import static com.endava.internship.infrastructure.util.ParkingLotConstants.ENTITIES_NOT_LINKED;
import static com.endava.internship.infrastructure.util.ParkingLotConstants.PARKING_LOT_ID_NOT_FOUND_ERROR_MESSAGE;
import static com.endava.internship.infrastructure.util.ParkingLotConstants.PARKING_LOT_ID_NOT_FOUND_MESSAGE;
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
    private final RoleRepository roleRepository;
    private final UserLinkToParkLotListener userLinkToParkLotListener;
    private final DaoMapper daoMapper;
    private final DtoMapper dtoMapper;
    private final UserUnlinkFromParkingLotListener userUnlinkFromParkLotListener;
    private final JdbcTemplate jdbcTemplate;

    private static void validateRequest(ParkingLotRequest parkingLotRequest) {
        if (parkingLotRequest.getEndTime().isBefore(parkingLotRequest.getStartTime())) {
            throw new DateTimeException("Starting hour cannot be after end hour");
        }

        List<String> weekdays = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        Set<String> savedDays = new HashSet<>();
        for (WorkingTimeDto workingTimeDto : parkingLotRequest.getWorkingTimesDto()) {
            String dayOfTheWeek = workingTimeDto.getNameDay();
            if (!weekdays.contains(dayOfTheWeek)) {
                throw new DateTimeException("Not a day of the week");
            }
            if (!savedDays.contains(dayOfTheWeek)) {
                savedDays.add(dayOfTheWeek);
            } else {
                throw new DateTimeException("Duplicated day of the week");
            }
        }

        if (parkingLotRequest.getParkingLevelsDto().size() > 5) {
            throw new InvalidRequestParameterException("Floors cannot exceed 5");
        }
    }

    @Override
    @Transactional
    public ResponseDto createParkingLot(ParkingLotRequest createParkingLotRequest) {
        String parkingLotName = createParkingLotRequest.getName();
        String parkingLotAddress = createParkingLotRequest.getAddress();

        if (parkingLotRepository.existsByName(parkingLotName) ||
                parkingLotRepository.existsByAddress(parkingLotAddress))
            throw new EntityExistsException("Parking Lot with the specified name or address already exists");

        validateRequest(createParkingLotRequest);

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
            for (int i = 1; i <= parkingLevelDto.getTotalSpots(); i++) {
                ParkingSpot parkingSpot = ParkingSpot.builder()
                        .parkingLevel(fetchedParkingLevelWithID)
                        .type("Regular")
                        .available(true)
                        .build();
                parkingSpotRepository.save(daoMapper.map(parkingSpot));
            }
            counterOfLevels++;

        }
        // Create and Save WorkingTimes
        for (WorkingTimeDto workingTimeDto : createParkingLotRequest.getWorkingTimesDto()) {
            WorkingTime workingTime = WorkingTime.builder()
                    .parkingLot(parkingLot)
                    .nameDay(workingTimeDto.getNameDay())
                    .build();
            workingTimeRepository.save(daoMapper.map(workingTime));
        }
        // Save ParkingLot
        parkingLotRepository.save(daoMapper.map(parkingLot));

        // return response
        return new ResponseDto("Parking Lot was created with success!");
    }

    @Override
    @Transactional
    public ResponseDto updateParkingLot(Integer id, ParkingLotRequest parkingLotRequest) {
        final ParkingLotEntity parkingLotEntity = parkingLotRepository.getParkingLotEntitiesById(id).
                orElseThrow(() -> new EntityNotFoundException(
                        String.format(PARKING_LOT_ID_NOT_FOUND_ERROR_MESSAGE, id)
                ));

        if (!parkingLotEntity.getName().equals(parkingLotRequest.getName()) && parkingLotRepository.existsByName(parkingLotRequest.getName())) {
            throw new EntityExistsException("Parking Lot with the specified name already exists");
        }

        if (!parkingLotEntity.getAddress().equals(parkingLotRequest.getAddress()) && parkingLotRepository.existsByAddress(parkingLotRequest.getAddress())) {
            throw new EntityExistsException("Parking Lot with the specified address already exists");
        }

        validateRequest(parkingLotRequest);

        workingTimeRepository.deleteByParkingLotId(parkingLotEntity.getId());
        for (WorkingTimeDto workingTimeDto : parkingLotRequest.getWorkingTimesDto()) {
            WorkingTime workingTime = WorkingTime.builder()
                    .parkingLot(daoMapper.map(parkingLotEntity))
                    .nameDay(workingTimeDto.getNameDay())
                    .build();
            workingTimeRepository.save(daoMapper.map(workingTime));
        }

        parkingLotEntity.setName(parkingLotRequest.getName());
        parkingLotEntity.setAddress(parkingLotRequest.getAddress());
        parkingLotEntity.setStartTime(parkingLotRequest.getStartTime());
        parkingLotEntity.setEndTime(parkingLotRequest.getEndTime());
        parkingLotEntity.setState(parkingLotRequest.isState());

        List<ParkingLevelEntity> parkingLevelEntity = parkingLevelRepository.getByParkingLotId(id);
        final List<ParkingLevelDto> parkingLevelDtoList = new ArrayList<>(parkingLotRequest.getParkingLevelsDto());

        int numberOfLevels = parkingLevelEntity.size();
        while (numberOfLevels < parkingLevelDtoList.size()) {
            Integer totalSpots = parkingLevelDtoList.get(numberOfLevels).getTotalSpots();

            ParkingLevelEntity levelBuild = ParkingLevelEntity.builder()
                    .parkingLot(parkingLotEntity)
                    .floor(++numberOfLevels)
                    .totalSpots(totalSpots)
                    .build();
            parkingLevelRepository.save(levelBuild);

            for (int i = 0; i < totalSpots; i++) {
                ParkingSpot parkingSpot = ParkingSpot.builder()
                        .parkingLevel(daoMapper.map(levelBuild))
                        .available(true)
                        .type("Regular")
                        .build();
                parkingSpotRepository.save(daoMapper.map(parkingSpot));
            }
        }

        while (numberOfLevels > parkingLevelDtoList.size()) {
            numberOfLevels--;
            Optional<List<ParkingSpotEntity>> spotsOpt = parkingSpotRepository.findByParkingLevelId(parkingLevelEntity.get(numberOfLevels).getId());
            if (spotsOpt.isPresent()) {
                List<ParkingSpotEntity> spots = spotsOpt.get();
                List<Integer> spotIds = spots.stream()
                        .map(ParkingSpotEntity::getId)
                        .toList();

                if (!spotIds.isEmpty()) {
                    parkingSpotRepository.deleteRelationUserSpotBySpotIds(spotIds);
                }
                parkingSpotRepository.deleteAll(spots);
            }
            parkingLevelRepository.delete(parkingLevelEntity.get(numberOfLevels));
        }

        parkingLevelEntity = parkingLevelRepository.getByParkingLotId(id);

        for (int i = 0; i < parkingLevelEntity.size(); i++) {
            ParkingLevelEntity levelEntity = parkingLevelEntity.get(i);
            ParkingLevelDto levelDto = parkingLevelDtoList.get(i);
            int difference = Math.abs(levelDto.getTotalSpots() - levelEntity.getTotalSpots());

            if (levelEntity.getTotalSpots() < levelDto.getTotalSpots()) {
                for (int j = 0; j < difference; j++) {
                    parkingSpotRepository.save(ParkingSpotEntity.builder()
                            .parkingLevel(levelEntity)
                            .available(true)
                            .type("Regular")
                            .build());
                }
            } else if (levelEntity.getTotalSpots() > levelDto.getTotalSpots()) {
                List<ParkingSpotEntity> spotEntity = parkingSpotRepository.getAllParkingSpotEntitiesByParkingLevelIdOrderByIdDesc(levelEntity.getId());
                for (int s = 0; s < difference; s++) {
                    Integer spotId = spotEntity.get(s).getId();

                    parkingSpotRepository.deleteRelationUserSpotBySpotId(spotId);
                    parkingSpotRepository.deleteParkingSpotEntityById(spotId);
                }
            }
            levelEntity.setTotalSpots(levelDto.getTotalSpots());
        }

        return new ResponseDto("Parking lot has been edited successfully");
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
                    .filter(val -> val)
                    .count();

            long totalSpots = parkingLevels.stream()
                    .mapToLong(ParkingLevelEntity::getTotalSpots)
                    .sum();

            final Set<WorkingTimeDto> workingTimeDtos = workingTimeRepository.findByParkingLot_Id(parkingLot.getId()).stream()
                    .map(daoMapper::map)
                    .map(dtoMapper::mapWorkingTimes)
                    .flatMap(Collection::stream)
                    .collect(toSet());

            ParkingLotDetailsDto response = new ParkingLotDetailsDto(parkingLot.getId(), parkingLot.getName(), parkingLot.getAddress(), parkingLot.getStartTime(), parkingLot.getEndTime(),
                    workingTimeDtos, parkingLot.isState(), totalSpots, unavailableSpots);
            parkingLotDetailsDtos.add(response);
        }
        return parkingLotDetailsDtos;
    }

    @Override
    @Transactional
    public UserToParkingLotDto linkUserToParkingLot(UpdateParkLotLinkRequest updateParkLotLinkRequest) {

        final String userEmail = updateParkLotLinkRequest.getUserEmail();
        final String parkingLotName = updateParkLotLinkRequest.getParkingLotName();

        final UserEntity userEntity = getUserEntity(userEmail);
        final ParkingLotEntity parkingLotEntity = getParkingLotEntity(parkingLotName);

        if (userEntity.getParkingLots().contains(parkingLotEntity)) {
            throw new EntityLinkException(String.format(ENTITIES_ALREADY_LINKED));
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
    public ResponseDto unlinkUserFromParkingLot(UpdateParkLotLinkRequest updateParkLotLinkRequest) {

        final String userEmail = updateParkLotLinkRequest.getUserEmail();
        final String parkingLotName = updateParkLotLinkRequest.getParkingLotName();

        final UserEntity userEntity = getUserEntity(userEmail);
        final ParkingLotEntity parkingLotEntity = getParkingLotEntity(parkingLotName);

        if (!userEntity.getParkingLots().contains(parkingLotEntity)) {
            throw new EntityLinkException(String.format(ENTITIES_NOT_LINKED));
        }

        userEntity.getParkingLots().remove(parkingLotEntity);

        final User userDomain = daoMapper.map(userEntity);

        final Set<ParkingLotEntity> userNewParkingLots = userEntity.getParkingLots();

        final UserToParkingLotDto emailDetails = new UserToParkingLotDto(userEntity.getCredential().getEmail(), userEntity.getName(), parkingLotEntity.getName(), parkingLotEntity.getAddress());

        if (!userNewParkingLots.contains(parkingLotEntity)) {
            userUnlinkFromParkLotListener.handleUserUnlinkFromParkLotEvent(emailDetails);
        }

        return new ResponseDto(userDomain.getName() + " has been unlinked");
    }

    @Override
    @Transactional
    public ResponseDto deleteParkingLot(Integer parkingLotId) {
        final ParkingLotEntity parkingLotEntity = parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(PARKING_LOT_NAME_NOT_FOUND_ERROR_MESSAGE, parkingLotId)));

        List<ParkingLevelEntity> levels = parkingLevelRepository.getByParkingLotId(parkingLotEntity.getId());

        for (ParkingLevelEntity level : levels) {
            Optional<List<ParkingSpotEntity>> spotsOpt = parkingSpotRepository.findByParkingLevelId(level.getId());
            if (spotsOpt.isPresent()) {
                List<ParkingSpotEntity> spots = spotsOpt.get();
                List<Integer> spotIds = spots.stream()
                        .map(ParkingSpotEntity::getId)
                        .toList();

                if (!spotIds.isEmpty()) {
                    parkingSpotRepository.deleteRelationUserSpotBySpotIds(spotIds);
                }
                parkingSpotRepository.deleteAll(spots);
            }
            parkingLevelRepository.delete(level);
        }

        Optional<Set<WorkingTimeEntity>> workingTimesOpt = workingTimeRepository.findByParkingLot_Id(parkingLotEntity.getId());
        if (workingTimesOpt.isPresent()) {
            Set<WorkingTimeEntity> workingTimes = workingTimesOpt.get();
            workingTimeRepository.deleteAll(workingTimes);
        }

        parkingLotRepository.deleteRelationUserSpotBySpotIds(parkingLotId);
        parkingLotRepository.delete(parkingLotEntity);

        return new ResponseDto("The parking lot with ID: " + parkingLotId + " and all its related entities has been deleted");
    }

    private UserEntity getUserEntity(String userEmail) {
        return userRepository.findByCredential_Email(userEmail)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(USER_EMAIL_NOT_FOUND_ERROR_MESSAGE, userEmail)));
    }

    private ParkingLotEntity getParkingLotEntity(String parkingLotName) {
        return parkingLotRepository.findByName(parkingLotName)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(PARKING_LOT_NAME_NOT_FOUND_ERROR_MESSAGE, parkingLotName)));
    }

    @Override
    @Transactional
    public ParkingLotDto getSpecificParkingLot(String userEmail, Integer parkingLotId) {

        final String userRole = userRepository.findByCredential_Email(userEmail).get().getRole().getName();
        final ParkingLotEntity parkingLotEntity = parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(PARKING_LOT_ID_NOT_FOUND_MESSAGE, parkingLotId)));
        final ParkingLot parkingLot = daoMapper.map(parkingLotEntity);
        final ParkingLotDto parkingLotDto = dtoMapper.map(parkingLot);

        final Optional<Set<WorkingTimeEntity>> workingTimeEntityOpt = workingTimeRepository.findByParkingLot_Id(parkingLotId);
        final Set<WorkingTimeEntity> workingTimeEntity = workingTimeEntityOpt.get();
        final Set<WorkingTime> workingTime = daoMapper.map(workingTimeEntity);
        final Set<WorkingTimeDto> workingTimeDto = dtoMapper.mapWorkingTimes(workingTime);

        final Set<ParkingLevelEntity> parkingLevelEntities = new HashSet<>(parkingLevelRepository.getByParkingLotId(parkingLotId));
        final Set<ParkingLevel> parkingLevels = daoMapper.mapParkingLevels(parkingLevelEntities);
        final Set<ParkingLevelDetailsDto> parkingLevelDetailsDtos = dtoMapper.map(parkingLevels);

        parkingLotDto.setWorkingTimes(workingTimeDto);
        parkingLotDto.setParkingLevel(parkingLevelDetailsDtos);

        if ("Admin".equals(userRole)) {
            for (ParkingLevelDetailsDto parkingLevelDetailsDto : parkingLevelDetailsDtos) {
                for (ParkingSpotDtoAdmin parkingSpotDtoAdmin : parkingLevelDetailsDto.getParkingSpots()) {
                    Optional<UserEntity> parkingSpotUser = userRepository.findUserEntityByParkingSpot_Id(parkingSpotDtoAdmin.getId());

                    parkingSpotUser.ifPresent(user -> {
                        parkingSpotDtoAdmin.setUserId(user.getId());
                        parkingSpotDtoAdmin.setUserPhone(user.getPhone());
                    });
                }
            }
        }
        return parkingLotDto;
    }
}