package com.fasthub.backend.admin.brand.entity;

import com.fasthub.backend.cmm.img.BaseImg;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "Brand_img")
public class BrandImg implements BaseImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brandimg_id")
    private Long id;

    @Column(nullable = false, name = "IMG_Path")
    private String imgPath;

    @Column(nullable = false, name = "IMG_NM")
    private String imgNm;

    @Column(nullable = false, name = "IMG_ORIGIN_NM")
    private String imgOriginNm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Override
    public void setImgNm(String imgNm) {
        this.imgNm = imgNm;
    }

    @Override
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public void setImgOriginNm(String imgOriginNm) {
        this.imgOriginNm = imgOriginNm;
    }

    @Override
    public void setMappingEntity(Object mappingEntity) {
        this.brand = (Brand)mappingEntity;
    }
}
