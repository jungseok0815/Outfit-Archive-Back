package com.fasthub.backend.admin.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Table(name = "product_size")
public class ProductSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "SIZE_NM", nullable = false)
    private String sizeNm;

    @Column(name = "QUANTITY", nullable = false)
    private int quantity;

    public void decreaseQuantity(int qty) {
        if (this.quantity < qty) {
            throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + this.quantity + ", 요청 수량: " + qty);
        }
        this.quantity -= qty;
    }

    public void increaseQuantity(int qty) {
        this.quantity += qty;
    }
}
