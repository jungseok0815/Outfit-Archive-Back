package com.fasthub.backend.user.review.dto;

import com.fasthub.backend.user.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ResponseReviewDto {

    private Long id;
    private Long orderId;
    private Long productId;
    private String productNm;
    private Long userId;
    private String userNm;
    private int rating;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<String> imgPaths;

    public static ResponseReviewDto of(Review review) {
        return ResponseReviewDto.builder()
                .id(review.getId())
                .orderId(review.getOrder().getId())
                .productId(review.getProduct().getId())
                .productNm(review.getProduct().getProductNm())
                .userId(review.getUser().getId())
                .userNm(review.getUser().getUserNm())
                .rating(review.getRating())
                .content(review.getContent())
                .createdDate(review.getCreatedDate())
                .updatedDate(review.getUpdatedDate())
                .imgPaths(review.getImages().stream().map(img -> img.getImgPath()).collect(Collectors.toList()))
                .build();
    }
}
