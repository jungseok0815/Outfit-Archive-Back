package com.fasthub.backend.cmm.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductCategory {
    // 상위 카테고리
    TOP("상의", "Tops"),
    BOTTOM("하의", "Bottoms"),
    OUTER("아우터", "Outer"),
    DRESS("원피스/세트", "Dress/Sets"),
    SHOES("신발", "Shoes"),
    BAG("가방", "Bags");

    private final String korName;
    private final String engName;

}
