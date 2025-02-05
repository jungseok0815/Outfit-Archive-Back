package com.fasthub.backend.oper.product.entity;

import com.fasthub.backend.cmm.img.BaseImg;
import com.fasthub.backend.oper.product.dto.ProductDto;
import com.fasthub.backend.oper.product.dto.ProductImgDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.security.Timestamp;
import java.util.Objects;


@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ProductImg")
@NoArgsConstructor
public class ProductImg implements BaseImg{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "IMG_Path")
    private String imgPath;

    @Column(nullable = false, name = "IMG_NM")
    private String imgNm;

    @Column(nullable = false, name = "IMG_ORIGIN_NM")
    private String imgOriginNm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // Product 설정 메서드
    protected void setProduct(Product product) {
        this.product = product;
    }


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
        this.product = (Product) mappingEntity;
    }


}
