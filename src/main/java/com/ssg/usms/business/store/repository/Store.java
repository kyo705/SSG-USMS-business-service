package com.ssg.usms.business.store.repository;

import com.ssg.usms.business.store.constant.StoreState;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(indexes = {
        @Index(name = "usms_store_business_license_img_id_idx", unique = true, columnList = "businessLicenseImgId"),
        @Index(name = "usms_store_unique_idx", unique = true, columnList = "userId, storeName, storeAddress, businessLicenseCode")
})
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
    private String adminComment;

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

    public void approve(String approvalMessage) {
        this.storeState = StoreState.APPROVAL;
        this.adminComment = approvalMessage;
    }

    public void disapprove(String disapprovalMessage) {
        this.storeState = StoreState.DISAPPROVAL;
        this.adminComment = disapprovalMessage;
    }

    public void lock(String adminComment) {
        this.storeState = StoreState.STOPPED;
        this.adminComment = adminComment;
    }
    public void update(String storeName,
                       String storeAddress,
                       String businessLicenseCode) {

        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.businessLicenseCode = businessLicenseCode;
        this.storeState = StoreState.READY;
    }


}
