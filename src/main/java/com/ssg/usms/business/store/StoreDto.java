package com.ssg.usms.business.store;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreDto {

    private long id;
    private long userId;
    private String name;
    private String address;
    private String businessLicenseCode; /* 사업자 등록 번호 */
    private String businessLicenseImgId;
    private StoreState storeState;
}
