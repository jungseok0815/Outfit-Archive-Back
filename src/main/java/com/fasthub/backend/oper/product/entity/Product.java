package com.fasthub.backend.oper.product.entity;

import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.cmm.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Entity
@Table(name = "Product")
@ToString
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "PRODUCT_NM", nullable = false)
    private String productNm;

    @Column(name = "PRODUCT_PRICE", nullable = false)
    private int productPrice;

    @Column(name ="PRODUCT_QUANTITY", nullable = false)
    private  int productAuantity;

    @Column(nullable = false, name = "CATEGORY")
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImg> images = new ArrayList<>();

    // 이미지 추가 메서드
    public void addImage(ProductImg image) {
        images.add(image);
        image.setProduct(this);
    }
}
