package com.ssg.usms.business.video.repository;

public interface StreamKeyRepository {

    String saveStreamKey(String streamKey);

    String saveStreamKey(String streamKey, long expireTimeSecond);

    boolean isExistingStreamKey(String streamKey);

    void updateExpireTime(String streamKey, long expireTimeSecond);

    String removeStreamKey(String streamKey);


}
