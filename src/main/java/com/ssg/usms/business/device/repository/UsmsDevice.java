package com.ssg.usms.business.device.repository;


import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@Table(name="usms_device" , indexes = {@Index(name = "usms_user_userid_idx", columnList = "user_id", unique = true)})
@Getter
@Setter
@Builder
public class UsmsDevice {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userid;

    @Column(name = "token", nullable = false)
    private String token;

}
