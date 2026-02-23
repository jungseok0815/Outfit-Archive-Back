package com.fasthub.backend.admin.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasthub.backend.admin.brand.entity.Brand;
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

    @Column(name = "PRODUCT_QUANTITY", nullable = false)
    private int productQuantity;

    @Column(nullable = false, name = "CATEGORY")
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<ProductImg> images = new ArrayList<>();

    public void update(String productNm, String productCode, int productPrice, int productQuantity, ProductCategory category, Brand brand) {
        this.productNm = productNm;
        this.productCode = productCode;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.category = category;
        this.brand = brand;
    }
}


