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
public class UserLinkToParkLotListenerTest {

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private UserLinkToParkLotListener userLinkToParkLotListener;

    @Test
    public void handleUserLinkToParkLotEvent_ShouldSendEmail() {
        UserToParkingLotDto userToParkingLotDto = new UserToParkingLotDto("example@mailinator.com", "userName", "parkingLotName", "parkingLotAddress");

        userLinkToParkLotListener.handleUserLinkToParkLotEvent(userToParkingLotDto);

        String subject = "Linking to a Parking Lot";
        String text = String.format("""
                Dear %s,

                You have been added as a user to the parking lot: "%s" located at address: "%s".

                Regards,
                Parking Lot Team
                """, userToParkingLotDto.getUserName(), userToParkingLotDto.getParkingLotName(), userToParkingLotDto.getParkingLotAddress());

        verify(emailSenderService, times(1))
                .sendEmail(userToParkingLotDto.getUserEmail(), subject, text);
    }
}