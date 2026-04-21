package com.fasthub.backend.admin.keyword.dto;

import com.fasthub.backend.cmm.enums.ProductCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResponseKeywordDto {
    private Long id;
    private String keyword;
    private ProductCategory category;
    private String categoryName;
    private boolean active;
    private LocalDateTime createdAt;
}
