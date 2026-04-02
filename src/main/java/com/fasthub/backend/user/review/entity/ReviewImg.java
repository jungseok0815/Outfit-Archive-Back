package com.fasthub.backend.user.review.entity;

import com.fasthub.backend.cmm.img.BaseImg;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "review_img")
public class ReviewImg implements BaseImg<Review> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imgPath;

    @Column(nullable = false)
    private String imgNm;

    @Column(nullable = false)
    private String imgOriginNm;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Override public void setImgNm(String imgNm) { this.imgNm = imgNm; }
    @Override public void setImgPath(String imgPath) { this.imgPath = imgPath; }
    @Override public void setImgOriginNm(String imgOriginNm) { this.imgOriginNm = imgOriginNm; }
    @Override public void setMappingEntity(Review review) { this.review = review; }
}
