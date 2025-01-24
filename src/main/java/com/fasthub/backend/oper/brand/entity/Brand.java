package com.fasthub.backend.oper.brand.entity;

import jakarta.persistence.*;

@Entity
@Table(name ="Brand")
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

    @Column(name = "BRAND_IMG")
    private String brandImg;
}
