package com.ssg.usms.business.login.persistence;


import com.ssg.usms.business.login.persistence.UserRole;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@DynamicInsert
@Table(name="usms_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsmsUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username", nullable = false, unique = true)
    private String username;

    @Column(name="password")
    private String password;

    @Column(name="person_name")
    private String personName;

    @Column(name="person_number",nullable = false,unique = true)
    private String phoneNumber;

    @Column(name="email",nullable = false,unique = true)
    private String email;

    @Builder.Default
    @Column(name = "security_state")
    private Integer securityState = 0;

    @Builder.Default
    @Column(name = "is_lock")
    private boolean isLock = false;

    @Builder.Default
    @Column(name= "role")
    private UserRole role = UserRole.STORE_OWNER;
}
