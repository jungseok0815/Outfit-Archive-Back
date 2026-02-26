package com.fasthub.backend.user.recommend.strategy;

public interface PopularProductProjection {
    Long getProductId();
    Long getOrderCount();
}
