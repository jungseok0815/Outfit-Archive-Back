package com.fasthub.backend.admin.keyword.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResponseKeywordDto {
    private Long id;
    private String keyword;
    private Long categoryId;
    private String categoryName;
    private boolean active;
    private LocalDateTime createdAt;
}
