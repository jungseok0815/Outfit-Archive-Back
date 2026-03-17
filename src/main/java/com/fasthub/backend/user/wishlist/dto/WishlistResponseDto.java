package com.fasthub.backend.user.wishlist.dto;

import com.fasthub.backend.user.wishlist.entity.Wishlist;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WishlistResponseDto {

    private Long wishlistId;
    private Long productId;
    private String productNm;
    private int productPrice;
    private String brandNm;
    private String imgPath;

    public static WishlistResponseDto of(Wishlist wishlist) {
        String imgPath = wishlist.getProduct().getImages().isEmpty()
                ? null
                : wishlist.getProduct().getImages().get(0).getImgPath();
        String brandNm = wishlist.getProduct().getBrand() != null
                ? wishlist.getProduct().getBrand().getBrandNm()
                : null;

        return WishlistResponseDto.builder()
                .wishlistId(wishlist.getId())
                .productId(wishlist.getProduct().getId())
                .productNm(wishlist.getProduct().getProductNm())
                .productPrice(wishlist.getProduct().getProductPrice())
                .brandNm(brandNm)
                .imgPath(imgPath)
                .build();
    }
}
