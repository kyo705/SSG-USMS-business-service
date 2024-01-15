package com.ssg.usms.business.video.repository;

import java.util.List;

public interface StreamKeyRepository {

    String saveStreamKey(String streamKey);
    List<String> findStreamKeys(int offset, int size);
    boolean isExistingStreamKey(String streamKey);
    String removeStreamKey(String streamKey);
}
