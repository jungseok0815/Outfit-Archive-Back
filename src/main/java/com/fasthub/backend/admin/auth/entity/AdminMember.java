package com.fasthub.backend.admin.auth.entity;

import com.fasthub.backend.cmm.enums.AdminRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "AdminMember")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "USER_ID", nullable = false, unique = true)
    private String memberId;

    @Column(nullable = false, name = "USER_NM")
    private String memberNm;

    @Column(nullable = false, name = "USER_PWD")
    private String memberPwd;

    @Column(name = "AFFILIATION")
    private String affiliation;

    @Column(nullable = false, name = "ADMIN_ROLE")
    @Enumerated(EnumType.STRING)
    private AdminRole adminRole;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
