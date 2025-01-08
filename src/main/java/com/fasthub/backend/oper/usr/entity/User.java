package com.fasthub.backend.oper.usr.entity;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.oper.usr.dto.JoinDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "Member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "USER_ID", nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, name = "USER_NM")
    private String userNm;

    @Column(nullable = false, name = "USER_PW")
    private String userPw;

    @Column(nullable = false, name = "USER_AGE")
    private long userAge;

    @Column(nullable = false, name = "AUTH_NAME")
    @Enumerated(EnumType.STRING)
    private UserRole authName;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {;
        return Collections.singleton(new SimpleGrantedAuthority(authName.name()));
    }

    @Override
    public String getPassword() {
        return userPw;
    }

    @Override
    public String getUsername() {
        return userId;
    }

}

