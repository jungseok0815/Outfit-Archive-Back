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

    @Column(unique = true, length = 100)
    private String tossOrderId;  // 토스에 전달하는 UUID 주문번호

    @Column(length = 200)
    private String paymentKey;   // 토스 결제 승인 후 받는 키

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public void confirmPayment(String paymentKey) {
        this.paymentKey = paymentKey;
        this.status = OrderStatus.PAYMENT_COMPLETE;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }
}
