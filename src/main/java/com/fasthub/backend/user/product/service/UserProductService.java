package com.fasthub.backend.user.product.service;

import com.fasthub.backend.admin.product.dto.ResponseProductDto;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.mapper.ProductMapper;
import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.product.repository.UserProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProductService {

    private final UserProductRepository userProductRepository;
    private final ProductMapper productMapper;

    public Page<ResponseProductDto> search(String keyword, ProductCategory category, Long brandId, Integer minPrice, Integer maxPrice, String sortBy, Pageable pageable) {
        if ("popular".equals(sortBy)) {
            return userProductRepository.searchProductsByPopularity(keyword, category, brandId, minPrice, maxPrice, pageable)
                    .map(productMapper::productToProductDto);
        }
        return userProductRepository.searchProducts(keyword, category, brandId, minPrice, maxPrice, pageable)
                .map(productMapper::productToProductDto);
    }

    public ResponseProductDto getDetail(Long id) {
        Product product = userProductRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_FAIL_SELECT));
        return productMapper.productToProductDto(product);
    }
}
