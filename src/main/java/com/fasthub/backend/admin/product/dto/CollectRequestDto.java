package com.fasthub.backend.admin.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CollectRequestDto {
    private List<Long> brandIds;
}
