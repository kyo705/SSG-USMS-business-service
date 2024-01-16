package com.ssg.usms.business.store.dto;

import com.ssg.usms.business.store.StoreState;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreDto {

    private long id;
    private long userId;
    private String name;
    private String address;
    private String businessLicenseCode; /* 사업자 등록 번호 */
    private String businessLicenseImgId;
    private StoreState storeState;
}
