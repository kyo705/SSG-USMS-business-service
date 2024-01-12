package com.ssg.usms.business.store;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CctvDto {

    private long id;
    private long storeId;
    private String cctvName;
    private String cctvStreamKey;
    private boolean isExpired;
}
