package com.endava.internship.infrastructure.listeners;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.endava.internship.infrastructure.service.EmailSenderService;
import com.endava.internship.web.dto.UserToParkingLotDto;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserUnlinkFromParkingLotListenerTest {

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private UserUnlinkFromParkingLotListener userUnlinkFromParkingLotListener;

    @Test
    public void handleUserUnlinkFromParkLotEvent_ShouldSendEmail() {
        UserToParkingLotDto userToParkingLotDto = new UserToParkingLotDto("example@mailinator.com", "userName", "parkingLotName", "parkingLotAddress");

        userUnlinkFromParkingLotListener.handleUserUnlinkFromParkLotEvent(userToParkingLotDto);

        String subject = "Unlinked from the Parking Lot";
        String text = String.format("""
                Dear %s,

                You have unlinked from the parking lot: "%s" located at address: "%s".

                Regards,
                Parking Lot Team
                """, userToParkingLotDto.getUserName(), userToParkingLotDto.getParkingLotName(), userToParkingLotDto.getParkingLotAddress());

        verify(emailSenderService, times(1))
                .sendEmail(userToParkingLotDto.getUserEmail(), subject, text);
    }
}
