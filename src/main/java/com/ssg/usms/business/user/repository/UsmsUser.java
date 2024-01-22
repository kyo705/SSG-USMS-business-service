package com.ssg.usms.business.user.repository;


import com.ssg.usms.business.user.dto.SecurityState;
import com.ssg.usms.business.user.dto.UserRole;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@DynamicInsert
@Table(name="usms_user" , indexes = {@Index(name = "usms_user_username_idx", columnList = "username", unique = true)})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UsmsUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username", nullable = false)
    private String username;

    @Column(name="password")
    private String password;

    @Column(name="person_name")
    private String personName;

    @Column(name="phone_number",nullable = false,unique = true)
    private String phoneNumber;

    @Column(name="email",nullable = false)
    private String email;

    @Builder.Default
    @Column(name = "security_state")
    private SecurityState securityState = SecurityState.BASIC;

    @Builder.Default
    @Column(name = "is_lock")
    private boolean isLock = false;

    @Builder.Default
    @Column(name= "role")
    private UserRole role = UserRole.STORE_OWNER;
}
