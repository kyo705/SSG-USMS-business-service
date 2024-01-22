package com.ssg.usms.business.store.dto;

import com.ssg.usms.business.store.constant.StoreState;
import com.ssg.usms.business.store.repository.Store;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreDto {

    private Long id;
    private long userId;
    private String name;
    private String address;
    private String businessLicenseCode; /* 사업자 등록 번호 */
    private String businessLicenseImgId;
    private StoreState storeState;

    public StoreDto(Store store) {
        this.id = store.getId();
        this.userId = store.getUserId();
        this.name = store.getStoreName();
        this.address = store.getStoreAddress();
        this.businessLicenseCode = store.getBusinessLicenseCode();
        this.businessLicenseImgId = store.getBusinessLicenseImgId();
        this.storeState = store.getStoreState();
    }
}
