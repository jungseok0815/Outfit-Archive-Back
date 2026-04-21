package com.fasthub.backend.admin.keyword.dto;

import com.fasthub.backend.cmm.enums.ProductCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertKeywordDto {
    private String keyword;
    private ProductCategory category;
}
