package com.ssg.usms.business.notification.service;


import com.ssg.usms.business.notification.exception.NotificationFailureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static com.ssg.usms.business.notification.constants.NotificationConstants.EMAIL_SEND_FAILURE_MESSAGE;

@Service
@RequiredArgsConstructor
public class EmailNotificationService implements NotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.sender}")
    private String sender;

    @Override
    public void send(String destination, String subject, String message) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sender);
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
