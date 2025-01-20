package com.fasthub.backend.oper.product.entity;
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
    private int productAuantity;

    @Column(nullable = false, name = "CATEGORY")
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImg> images = new ArrayList<>();


}
