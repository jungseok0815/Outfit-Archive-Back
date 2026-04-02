package com.fasthub.backend.user.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class InsertReviewDto {

    private Long orderId;
    private int rating;
    private String content;
    private List<MultipartFile> images;
}
