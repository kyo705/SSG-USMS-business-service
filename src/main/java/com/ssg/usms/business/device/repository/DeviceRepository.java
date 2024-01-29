package com.ssg.usms.business.device.repository;


public interface DeviceRepository {

    void saveToken(UsmsDevice device);

    int deleteToken(Long userid);
}
