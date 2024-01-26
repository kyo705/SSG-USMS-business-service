package com.ssg.usms.business.device.repository;


public interface DeviceRepository {

    void saveToken(UserDevice device);

    int deleteToken(Long userid);
}
