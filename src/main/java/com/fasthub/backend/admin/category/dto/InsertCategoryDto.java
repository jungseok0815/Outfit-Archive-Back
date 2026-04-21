package com.fasthub.backend.admin.category.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertCategoryDto {
    private String name;
    private String korName;
    private String engName;
    private String defaultSizes;
}
