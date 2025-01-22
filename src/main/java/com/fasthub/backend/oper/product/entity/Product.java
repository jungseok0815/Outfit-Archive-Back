package com.fasthub.backend.oper.product.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasthub.backend.cmm.enums.ProductCategory;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
@NoArgsConstructor
@Entity
@ToString

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "PRODUCT_NM", nullable = false)
    private String productNm;

    @Column(name = "PRODUCT_CODE", nullable = false)
    private String productCode;

    @Column(name = "PRODUCT_PRICE", nullable = false)
    private int productPrice;

    @Column(name ="PRODUCT_QUANTITY", nullable = false)
    private int productQuantity;

    @Column(name = "PRODUCT_BRAND", nullable = false)
    private String productBrand;

    @Column(nullable = false, name = "CATEGORY")
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // 순환 방지
    private List<ProductImg> images = new ArrayList<>();
}
