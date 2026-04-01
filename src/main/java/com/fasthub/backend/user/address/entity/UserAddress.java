package com.fasthub.backend.user.address.entity;

import com.fasthub.backend.user.address.dto.InsertAddressDto;
import com.fasthub.backend.user.usr.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_address")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String recipientName;

    @Column(nullable = false, length = 20)
    private String recipientPhone;

    @Column(nullable = false, length = 10)
    private String zipCode;

    @Column(nullable = false, length = 200)
    private String baseAddress;

    @Column(length = 100)
    private String detailAddress;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDefault = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public void update(String recipientName, String recipientPhone, String zipCode, String baseAddress, String detailAddress) {
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.zipCode = zipCode;
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
