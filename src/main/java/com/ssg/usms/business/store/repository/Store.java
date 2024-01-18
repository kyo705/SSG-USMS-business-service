package com.ssg.usms.business.store.repository;

import com.ssg.usms.business.store.constant.StoreState;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long userId;
    private String storeName;
    private String storeAddress;
    private String businessLicenseCode;             /* 사업자 등록 번호 */
    private String businessLicenseImgId;
    @Convert(converter = StoreStateConverter.class)
    private StoreState storeState;

    public static Store init(long userId,
                             String storeName,
                             String storeAddress,
                             String businessLicenseCode,
                             String businessLicenseImgId) {

        Store store = new Store();
        store.userId = userId;
        store.storeName = storeName;
        store.storeAddress = storeAddress;
        store.businessLicenseCode = businessLicenseCode;
        store.businessLicenseImgId = businessLicenseImgId;
        store.storeState = StoreState.READY;

        return store;
    }

    public void approve() {
        this.storeState = StoreState.APPROVAL;
    }

    public void disapprove() {
        this.storeState = StoreState.DISAPPROVAL;
    }

    public void updateStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void retry(String storeName,
                      String storeAddress,
                      String businessLicenseCode,
                      String businessLicenseImgId) {

        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.businessLicenseCode = businessLicenseCode;
        this.businessLicenseImgId = businessLicenseImgId;
        this.storeState = StoreState.READY;
    }
}
