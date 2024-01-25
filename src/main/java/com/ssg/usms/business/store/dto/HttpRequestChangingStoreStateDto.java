package com.ssg.usms.business.store.dto;

import com.ssg.usms.business.store.constant.StoreState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpRequestChangingStoreStateDto {

    private StoreState state;
    private String message;
}
