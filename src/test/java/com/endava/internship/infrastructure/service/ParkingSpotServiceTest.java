package com.endava.internship.infrastructure.service;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.endava.internship.dao.entity.ParkingSpotEntity;
import com.endava.internship.dao.repository.ParkingSpotRepository;
import com.endava.internship.infrastructure.domain.ParkingSpot;
import com.endava.internship.infrastructure.exception.InvalidRequestParameterException;
import com.endava.internship.infrastructure.mapper.DaoMapper;
import com.endava.internship.web.dto.ResponseDto;
import com.endava.internship.web.request.UpdateParkingSpotRequest;

import static com.endava.internship.infrastructure.utils.TestUtils.SPOT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotServiceTest {

    @Mock
    private DaoMapper daoMapper;
    @Mock
    private ParkingSpotRepository parkingSpotRepository;
    @InjectMocks
    private ParkingSpotServiceImpl parkingSpotService;

    @Test
    public void whenEditParkingSpotCalled_ShouldReturnUpdatedDto() {
        final UpdateParkingSpotRequest spotToUpdate = new UpdateParkingSpotRequest("New Type");

        final ParkingSpotEntity existingSpot = new ParkingSpotEntity();
        existingSpot.setId(SPOT_ID);
        existingSpot.setType("Old Type");
        existingSpot.setAvailable(true);

        final ParkingSpotEntity updatedSpot = new ParkingSpotEntity();
        updatedSpot.setId(SPOT_ID);
        updatedSpot.setType(spotToUpdate.getType());

        final ParkingSpot parkingSpot = new ParkingSpot(null, existingSpot.isAvailable(), "New Type", null);

        when(parkingSpotRepository.findById(SPOT_ID)).thenReturn(Optional.of(existingSpot));
        when(daoMapper.map(any(ParkingSpotEntity.class))).thenReturn(parkingSpot);
        when(parkingSpotRepository.save(any(ParkingSpotEntity.class))).thenReturn(updatedSpot);

        final ResponseDto result = parkingSpotService.editParkingSpot(SPOT_ID, spotToUpdate);

        assertEquals(spotToUpdate.getType(), result.getMessage());
        verify(parkingSpotRepository).findById(SPOT_ID);
        verify(parkingSpotRepository).save(any(ParkingSpotEntity.class));
    }

    @Test
    public void whenEditParkingSpotCalled_ShouldThrowInvalidRequestParameterException() {
        final ParkingSpotEntity existingSpot = new ParkingSpotEntity();
        existingSpot.setId(SPOT_ID);
        existingSpot.setAvailable(false);

        final ParkingSpot parkingSpot = new ParkingSpot(null, false, "New type", null);

        when(daoMapper.map(existingSpot)).thenReturn(parkingSpot);
        when(parkingSpotRepository.findById(SPOT_ID)).thenReturn(Optional.of(existingSpot));

        assertThrows(InvalidRequestParameterException.class, () ->
                parkingSpotService.editParkingSpot(SPOT_ID, any(UpdateParkingSpotRequest.class))
        );

        verify(parkingSpotRepository).findById(SPOT_ID);
        verify(parkingSpotRepository, times(0)).save(any(ParkingSpotEntity.class));
    }
}