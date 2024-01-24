package com.ssg.usms.business.notification.service;

public interface NotificationService {

    void send(String destination,String subject, String message);

}
