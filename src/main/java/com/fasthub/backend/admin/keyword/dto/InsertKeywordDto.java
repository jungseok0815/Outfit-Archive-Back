package com.fasthub.backend.admin.keyword.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertKeywordDto {
    private String keyword;
    private Long categoryId;
}
