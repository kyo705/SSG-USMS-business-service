package com.ssg.usms.business.video.repository;

public interface StreamKeyRepository {

    String saveStreamKey(String streamKey);

    boolean isExistingStreamKey(String streamKey);

    String removeStreamKey(String streamKey);
}
