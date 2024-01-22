package com.ssg.usms.business.cctv.repository;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(indexes = @Index(name = "usms_cctv_stream_key_idx", unique = true, columnList = "cctv_stream_key"))
public class Cctv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "cctv_name")
    private String name;

    @Column(name = "cctv_stream_key")
    private String streamKey;

    @Column(name = "is_expired")
    private boolean isExpired;
}
