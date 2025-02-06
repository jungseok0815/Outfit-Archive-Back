package com.fasthub.backend.oper.order.entity;

import com.fasthub.backend.cmm.enums.OrderStatus;
import com.fasthub.backend.oper.auth.entity.User;
import com.fasthub.backend.oper.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "Sale")
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Product product;

    private LocalDateTime orderDate;
    private int orderPrice;  // 주문 당시 가격
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
