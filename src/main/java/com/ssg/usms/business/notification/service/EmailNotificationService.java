package com.ssg.usms.business.notification.service;


import com.ssg.usms.business.notification.exception.NotificationFailureException;
import lombok.RequiredArgsConstructor;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static com.ssg.usms.business.notification.constants.NotificationConstants.EMAIL_SEND_FAILURE_MESSAGE;
import static com.ssg.usms.business.notification.constants.NotificationConstants.IDENTIFICATION_EMAIL_SENDER;

@Service
@RequiredArgsConstructor
public class EmailNotificationService implements NotificationService {

    private final JavaMailSender mailSender;
    @Override
    public void send(String destination, String subject, String message) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(IDENTIFICATION_EMAIL_SENDER);
        simpleMailMessage.setTo(destination);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        try{

            mailSender.send(simpleMailMessage);
        }
        catch (MailException e){

            throw new NotificationFailureException(EMAIL_SEND_FAILURE_MESSAGE);
        }
    }
}
