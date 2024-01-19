package com.ssg.usms.business.notification;


import com.ssg.usms.business.notification.util.SmsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("smsService")
@RequiredArgsConstructor
public class SmsNotificationService implements NotificationService{

    private final SmsUtil smsUtil;

    @Override
    public void send(String destination, String subject, String message) {

        smsUtil.sendOne(destination,subject,message);
    }



}
