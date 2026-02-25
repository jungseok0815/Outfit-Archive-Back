package com.fasthub.backend.user.post.entity;

import com.fasthub.backend.cmm.img.BaseImg;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post_img")
public class PostImg implements BaseImg<Post> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imgPath;

    @Column(nullable = false)
    private String imgNm;

    @Column(nullable = false)
    private String imgOriginNm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Override public void setImgNm(String imgNm) { this.imgNm = imgNm; }
    @Override public void setImgPath(String imgPath) { this.imgPath = imgPath; }
    @Override public void setImgOriginNm(String imgOriginNm) { this.imgOriginNm = imgOriginNm; }
    @Override public void setMappingEntity(Post post) { this.post = post; }
}
