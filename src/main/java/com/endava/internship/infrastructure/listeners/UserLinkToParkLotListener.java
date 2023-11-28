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

    }
}