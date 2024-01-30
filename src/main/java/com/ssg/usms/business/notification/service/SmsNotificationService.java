package com.ssg.usms.business.notification.service;


import com.ssg.usms.business.notification.exception.NotificationFailureException;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.ssg.usms.business.notification.constants.NotificationConstants.IDENTIFICATION_MESSAGE;
import static com.ssg.usms.business.notification.constants.NotificationConstants.SMS_SEND_SUCCESS_MESSAGE;


@Service
@RequiredArgsConstructor
public class SmsNotificationService implements NotificationService {


    @Value("${coolsms.api.key}")
    private String apiKey;
    @Value("${coolsms.api.secret}")
    private String apiSecretKey;
    @Value("${coolsms.sender}")
    private String sender;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init(){

        this.messageService = NurigoApp.INSTANCE.initialize(apiKey,apiSecretKey,"https://api.coolsms.co.kr" );
    }

    @Async("threadPoolTaskExecutor")
    @Override
    public void send(String destination, String subject, String message) {

        Message messageForm = new Message();

        messageForm.setFrom(sender);
        messageForm.setTo(destination);
        messageForm.setSubject(subject);
        messageForm.setText(IDENTIFICATION_MESSAGE + message);

        try{
            this.messageService.sendOne(new SingleMessageSendingRequest(messageForm));
        }
        catch (Exception e){
            throw new NotificationFailureException(SMS_SEND_SUCCESS_MESSAGE);
        }
    }



}
