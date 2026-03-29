package com.fasthub.backend.admin.brand.entity;

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

    @Column(name = "BANNER_IMG_PATH")
    private String bannerImgPath;

    @Column(name = "BANNER_IMG_NM")
    private String bannerImgNm;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BrandImg> images = new ArrayList<>();

    public void update(String brandNm, String brandNum, String brandLocation, String brandDc) {
        this.brandNm = brandNm;
        this.brandNum = brandNum;
        this.brandLocation = brandLocation;
        this.brandDc = brandDc;
    }

    public void updateBannerImg(String bannerImgPath, String bannerImgNm) {
        this.bannerImgPath = bannerImgPath;
        this.bannerImgNm = bannerImgNm;
    }
}
