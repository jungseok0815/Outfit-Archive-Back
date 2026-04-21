package com.fasthub.backend.admin.category.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCategoryDto {
    private Long id;
    private String korName;
    private String engName;
    private String defaultSizes;
}
