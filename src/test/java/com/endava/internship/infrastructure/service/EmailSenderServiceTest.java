package com.endava.internship.infrastructure.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailSenderService emailSenderService;

    @Test
    public void testSendEmail() {

        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Body";

        emailSenderService.sendEmail(to, subject, text);

        verify(javaMailSender).send(getExpectedSimpleMailMessage(to, subject, text));
    }

    private SimpleMailMessage getExpectedSimpleMailMessage(String to, String subject, String text) {
        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setTo(to);
        expectedMessage.setSubject(subject);
        expectedMessage.setText(text);
        return expectedMessage;
    }
}