package com.endava.internship.infrastructure.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.endava.internship.infrastructure.service.EmailSenderService;
import com.endava.internship.web.dto.UserToParkingLotDto;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserLinkToParkLotListener {

    private EmailSenderService emailSenderService;

    @EventListener
    public void handleUserLinkToParkLotEvent(UserToParkingLotDto emailDetails) {
        final String userName = emailDetails.getUserName();
        final String parkingLotName = emailDetails.getParkingLotName();
        final String parkingLotAddress = emailDetails.getParkingLotAddress();

        String subject = "Linking to a Parking Lot";
        String text = String.format("""
                Dear %s,

                You have been added as a user to the parking lot: "%s" located at address: "%s".

                Regards,
                Parking Lot Team
                """, userName, parkingLotName, parkingLotAddress);

        emailSenderService.sendEmail(emailDetails.getUserEmail(), subject, text);
    }
}