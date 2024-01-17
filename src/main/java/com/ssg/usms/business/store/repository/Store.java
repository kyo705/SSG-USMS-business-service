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
}
