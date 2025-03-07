package com.fasthub.backend.oper.brand.entity;

import com.fasthub.backend.oper.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name ="Brand")
@NoArgsConstructor
@ToString
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "BRAND_NM", nullable = false)
    private String brandNm;

    @Column(name = "BRAND_NUM")
    private String brandNum;

    @Column(name = "BRAND_LOCATION")
    private String brandLocation;

    @Column(name = "BRAND_DC")
    private String brandDc;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BrandImg> images = new ArrayList<>();

}
