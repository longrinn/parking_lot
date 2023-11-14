package com.endava.internship.infrastructure.listeners;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.endava.internship.infrastructure.service.EmailSenderService;

@ExtendWith(MockitoExtension.class)
public class UserRoleChangeEmailListenerTest {

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private UserRoleChangeEmailListener userRoleChangeEmailListener;

    @Test
    public void handleUserRoleChangeEvent_ShouldSendEmail() {
        String email = "test@example.com";

        userRoleChangeEmailListener.handleUserRoleChangeEvent(email);

        verify(emailSenderService, times(1))
                .sendEmail(eq(email), anyString(), anyString());
    }
}