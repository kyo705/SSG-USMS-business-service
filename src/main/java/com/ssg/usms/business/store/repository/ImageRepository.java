package com.ssg.usms.business.store.repository;

import java.io.InputStream;

public interface ImageRepository {

    void save(String key, InputStream inputStream);

    byte[] find(String key);

    boolean isExisting(String key);
}
