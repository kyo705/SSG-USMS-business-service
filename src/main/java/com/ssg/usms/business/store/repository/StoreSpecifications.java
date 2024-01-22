package com.ssg.usms.business.store.repository;

import com.ssg.usms.business.store.constant.StoreState;
import org.springframework.data.jpa.domain.Specification;

public class StoreSpecifications {

    static Specification<Store> hasUserId(Long userId) {

        return (store, cq, cb) -> cb.equal(store.get("userId"), userId);
    }

    static Specification<Store> hasBusinessLicenseCode(String businessLicenseCode) {

        return (store, cq, cb) -> cb.equal(store.get("businessLicenseCode"), businessLicenseCode);
    }

    static Specification<Store> hasUserState(StoreState state) {

        return (store, cq, cb) -> cb.equal(store.get("storeState"), state.getCode());
    }
}
