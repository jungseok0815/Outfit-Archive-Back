package com.fasthub.backend.admin.order.dto;

import lombok.Getter;

@Getter
public class RevenueByBrandDto {
    private Long brandId;
    private String brandNm;
    private Long orderCount;
    private Long totalRevenue;

    public RevenueByBrandDto(Long brandId, String brandNm, Long orderCount, Long totalRevenue) {
        this.brandId = brandId;
        this.brandNm = brandNm;
        this.orderCount = orderCount;
        this.totalRevenue = totalRevenue;
    }
}
