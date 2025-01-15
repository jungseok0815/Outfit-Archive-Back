package com.fasthub.backend.oper.product.entity;

import jakarta.persistence.*;
import lombok.*;
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

    @Column(nullable = false, name = "IMG_Path")
    private String imgPath;

    @Column(nullable = false, name = "IMG_NM")
    private String imgNm;

    @CreationTimestamp
    private Timestamp createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // Product 설정 메서드
    protected void setProduct(Product product) {
        this.product = product;
    }

    @Builder
    public ProductImg(String imgPath, String imgNm, Product product) {
        this.imgPath = imgPath;
        this.imgNm = imgNm;
        this.product = product;
    }

}
