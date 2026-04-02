package com.fasthub.backend.admin.order.entity;

import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.cmm.enums.OrderStatus;
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
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false)
    private String shippingAddress;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String recipientPhone;

    @Column(nullable = false)
    private int usedPoint;

    @Column(nullable = false, columnDefinition = "int not null default 0")
    private int couponDiscount = 0;  // 쿠폰 할인 금액 (미사용 시 0)

    @Column
    private Long userCouponId;       // 사용된 UserCoupon ID (취소 시 복원용, null이면 쿠폰 미사용)

    @Column(unique = true, length = 100)
    private String tossOrderId;  // 토스에 전달하는 UUID 주문번호

    @Column(length = 200)
    private String paymentKey;   // 토스 결제 승인 후 받는 키

    @Column(length = 100)
    private String trackingNumber;  // 운송장 번호

    @Column(length = 20)
    private String sizeNm;  // 선택 사이즈 (사이즈 없는 상품은 null)

    @Column
    private LocalDateTime deliveredAt;  // 배송완료 처리 일시

    public void updateStatus(OrderStatus status) {
        this.status = status;
        if (status == OrderStatus.DELIVERED) {
            this.deliveredAt = LocalDateTime.now();
        }
    }

    public void registerShipping(String trackingNumber) {
        this.trackingNumber = trackingNumber;
        this.status = OrderStatus.SHIPPING;
    }

    public void confirmPayment(String paymentKey) {
        this.paymentKey = paymentKey;
        this.status = OrderStatus.PAYMENT_COMPLETE;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }
}
