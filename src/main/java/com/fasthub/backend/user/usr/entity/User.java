package com.fasthub.backend.user.usr.entity;

import com.fasthub.backend.cmm.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "Member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "USER_ID", nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, name = "USER_NM")
    private String userNm;

    @Column(nullable = false, name = "USER_PWD")
    private String userPwd;

    @Column(nullable = false, name = "USER_AGE")
    private int userAge;

    @Column(nullable = false, name = "AUTH_NAME")
    @Enumerated(EnumType.STRING)
    private UserRole authName;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Column(name = "BIO", length = 200)
    private String bio;

    @Column(name = "PROFILE_IMG_NM")
    private String profileImgNm;

    @Column(name = "POINT", nullable = false)
    @Builder.Default
    private int point = 0;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void earnPoint(int amount) {
        this.point += amount;
    }

    public void usePoint(int amount) {
        if (this.point < amount) {
            throw new IllegalArgumentException("포인트 잔액이 부족합니다.");
        }
        this.point -= amount;
    }

    public void update(String userNm, int userAge, String encodedPwd, String bio) {
        this.userNm = userNm;
        this.userAge = userAge;
        this.userPwd = encodedPwd;
        this.bio = bio;
    }

    public void updateProfileImg(String profileImgNm) {
        this.profileImgNm = profileImgNm;
    }
}

