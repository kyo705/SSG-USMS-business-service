package com.ssg.usms.business.notification.service;

import java.io.IOException;

public interface NotificationService {

    void send(String destination,String subject, String message) throws IOException;

}
