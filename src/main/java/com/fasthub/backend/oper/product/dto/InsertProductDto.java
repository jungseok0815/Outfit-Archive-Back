package com.fasthub.backend.oper.product.dto;

import com.fasthub.backend.cmm.enums.ProductCategory;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class InsertProductDto {
    private String productName;
    private ProductCategory category;
    private int price;
    private String description;
    private List<MultipartFile> image;
}
