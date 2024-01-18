package com.ssg.usms.business.store.repository;

import com.ssg.usms.business.store.constant.StoreState;

import java.util.List;

public interface StoreRepository {

    Store save(Store store);
    List<Store> findAll(Long userId, String businessCode, StoreState state, int offset, int size);

    List<Store> findByUserId(Long userId, int offset, int size);
}
