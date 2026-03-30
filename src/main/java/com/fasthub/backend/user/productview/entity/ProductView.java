package com.fasthub.backend.user.productview.entity;

import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.user.usr.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Table(name = "product_view", indexes = {
        @Index(name = "idx_product_view_user", columnList = "user_id"),
        @Index(name = "idx_product_view_product", columnList = "product_id"),
        @Index(name = "idx_product_view_viewed_at", columnList = "viewed_at")
})
public class ProductView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;
}
