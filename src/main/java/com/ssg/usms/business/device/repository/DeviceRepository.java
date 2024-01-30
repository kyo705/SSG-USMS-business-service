package com.ssg.usms.business.device.repository;


import java.util.List;

public interface DeviceRepository {

    List<UsmsDevice> findByUserId(Long userId);

    void saveToken(UsmsDevice device);

    int deleteToken(Long userid);
}
