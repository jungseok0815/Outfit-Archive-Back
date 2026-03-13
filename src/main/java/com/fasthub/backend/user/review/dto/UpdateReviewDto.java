package com.fasthub.backend.user.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateReviewDto {

    private int rating;
    private String content;
}
