package com.fasthub.backend.user.coupon.entity;

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
@Table(name = "user_coupon", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "coupon_id"})  // DB 레벨 중복 발급 방지
})
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Column(nullable = false)
    private boolean isUsed;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime issuedAt;

    private LocalDateTime usedAt;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    // 결제 완료 시 사용 처리
    public void use() {
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
    }

    // 결제 취소 시 쿠폰 복원
    public void restore() {
        this.isUsed = false;
        this.usedAt = null;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }
}
