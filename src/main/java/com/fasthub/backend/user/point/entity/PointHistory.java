package com.fasthub.backend.user.point.entity;

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
@Table(name = "point_history")
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int amount;         // 양수: 적립, 음수: 사용

    @Column(nullable = false)
    private int balanceAfter;   // 변경 후 잔액

    @Column(nullable = false, length = 100)
    private String description; // 예: "상품 구매 적립", "포인트 사용"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointType type;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public enum PointType {
        EARN, USE
    }
}
