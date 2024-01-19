package com.ssg.usms.business.notification;

public interface NotificationService {

    void send(String destination,String subject, String message);

}
