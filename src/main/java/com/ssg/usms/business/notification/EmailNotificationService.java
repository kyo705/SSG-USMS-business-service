package com.ssg.usms.business.notification;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Qualifier("emailService")
@Service
public class EmailNotificationService implements NotificationService {

    @Override
    public void send(String destination, String subject, String message) {

    }
}
