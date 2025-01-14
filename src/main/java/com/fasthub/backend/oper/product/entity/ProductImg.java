package com.fasthub.backend.oper.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.security.Timestamp;


@Entity
@Getter
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "IMG_URL")
    private String imageUrl;

    @Column(nullable = false, name = "IMG_NM")
    private String imgNm;

    @Column(nullable = false, name = "ORIGIN_IMG_NM")
    private String originImgNm;

    @CreationTimestamp
    private Timestamp createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // Product 설정 메서드
    protected void setProduct(Product product) {
        this.product = product;
    }
}
