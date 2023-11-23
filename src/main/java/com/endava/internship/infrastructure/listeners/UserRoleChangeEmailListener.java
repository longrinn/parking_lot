package com.endava.internship.infrastructure.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.endava.internship.infrastructure.service.EmailSenderService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserRoleChangeEmailListener {

    private EmailSenderService emailSenderService;

    @EventListener
    public void handleUserRoleChangeEvent(String email) {
        String subject = "Role Change Notification";
        String text = """
                You have been granted an Admin role for Parking Lot app.
                Regards,
                    Parking Lot""";

        emailSenderService.sendEmail(email, subject, text);
    }
}
