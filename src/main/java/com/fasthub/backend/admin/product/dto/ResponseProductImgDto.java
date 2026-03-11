package com.fasthub.backend.admin.product.dto;

import com.fasthub.backend.admin.product.entity.ProductImg;
import lombok.Getter;

@Getter
public class ResponseProductImgDto {
    private Long id;
    private String imgPath;
    private String imgNm;
    private String imgOriginNm;

    public ResponseProductImgDto(ProductImg productImg) {
        this.id = productImg.getId();
        this.imgPath = productImg.getImgPath();
        this.imgNm = productImg.getImgNm();
        this.imgOriginNm = productImg.getImgOriginNm();
    }
}
