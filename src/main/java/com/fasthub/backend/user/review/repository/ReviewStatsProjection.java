package com.fasthub.backend.user.review.repository;

public interface ReviewStatsProjection {
    Long getProductId();
    Long getReviewCount();
    Double getAvgRating();
}
