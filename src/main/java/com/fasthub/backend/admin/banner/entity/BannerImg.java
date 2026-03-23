package com.fasthub.backend.admin.banner.entity;

import com.fasthub.backend.cmm.img.BaseImg;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Table(name = "banner_img")
public class BannerImg implements BaseImg<Banner> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "IMG_PATH")
    private String imgPath;

    @Column(nullable = false, name = "IMG_NM")
    private String imgNm;

    @Column(nullable = false, name = "IMG_ORIGIN_NM")
    private String imgOriginNm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banner_id")
    private Banner banner;

    @Override
    public void setImgNm(String imgNm) { this.imgNm = imgNm; }

    @Override
    public void setImgPath(String imgPath) { this.imgPath = imgPath; }

    @Override
    public void setImgOriginNm(String imgOriginNm) { this.imgOriginNm = imgOriginNm; }

    @Override
    public void setMappingEntity(Banner banner) { this.banner = banner; }
}
