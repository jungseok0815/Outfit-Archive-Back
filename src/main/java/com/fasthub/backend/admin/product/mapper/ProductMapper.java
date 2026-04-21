package com.fasthub.backend.admin.product.mapper;

import com.fasthub.backend.admin.category.dto.ResponseCategoryDto;
import com.fasthub.backend.admin.category.entity.Category;
import com.fasthub.backend.admin.product.dto.ProductSizeDto;
import com.fasthub.backend.admin.product.dto.ResponseProductDto;
import com.fasthub.backend.admin.product.dto.ResponseProductImgDto;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.entity.ProductImg;
import com.fasthub.backend.admin.product.entity.ProductSize;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "brandId", source = "brand.id")
    @Mapping(target = "brandNm", source = "brand.brandNm")
    @Mapping(target = "images", source = "images")
    @Mapping(target = "sizes", source = "sizes")
    @Mapping(target = "category", source = "category")
    ResponseProductDto productToProductDto(Product product);

    ResponseProductImgDto productImgToDto(ProductImg productImg);

    ProductSizeDto productSizeToDto(ProductSize productSize);

    default ResponseCategoryDto categoryToDto(Category category) {
        if (category == null) return null;
        ResponseCategoryDto dto = new ResponseCategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setKorName(category.getKorName());
        dto.setEngName(category.getEngName());
        dto.setDefaultSizes(category.getDefaultSizes());
        dto.setActive(category.isActive());
        return dto;
    }
}
