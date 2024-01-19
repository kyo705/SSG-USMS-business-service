package com.ssg.usms.business.notification.util;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.ssg.usms.business.notification.constants.NotificationConstants.IDENTIFICATION_MESSAGE;
import static com.ssg.usms.business.notification.constants.NotificationConstants.IDENTIFICATION_SMS_SENDER;

@Component
public class SmsUtil {

    @Value("${coolsms.api.key}")
    private String apiKey;
    @Value("${coolsms.api.secret}")
    private String apiSecretKey;
    private DefaultMessageService messageService;

    @PostConstruct
    private void init(){

        this.messageService = NurigoApp.INSTANCE.initialize(apiKey,apiSecretKey,"https://api.coolsms.co.kr" );
    }

    public SingleMessageSentResponse sendOne(String to,String subject, String verificationCode) {

        Message message = new Message();

        message.setFrom(IDENTIFICATION_SMS_SENDER);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(IDENTIFICATION_MESSAGE + verificationCode);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        return response;
    }



}
