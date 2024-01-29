package com.ssg.usms.business.cctv.dto;

import com.ssg.usms.business.cctv.repository.Cctv;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CctvDto {

    private long id;
    private long storeId;
    private String cctvName;
    private String cctvStreamKey;
    private boolean isExpired;
    private Boolean isConnected;

    public CctvDto(Cctv cctv) {

        this.id = cctv.getId();
        this.storeId = cctv.getStoreId();
        this.cctvName = cctv.getName();
        this.cctvStreamKey = cctv.getStreamKey();
        this.isExpired = cctv.isExpired();
        this.isConnected = false;
    }
}
