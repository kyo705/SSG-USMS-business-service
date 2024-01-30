package com.ssg.usms.business.video.repository;

import java.util.List;

public interface VideoRepository {

    byte[] getVideo(String key);

    List<String> getVideoFilenames(String path);
}
