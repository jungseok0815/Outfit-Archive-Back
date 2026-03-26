package com.fasthub.backend.user.product.service;

import com.fasthub.backend.admin.product.dto.ResponseProductDto;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.mapper.ProductMapper;
import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.user.product.repository.UserProductRepository;
import com.fasthub.backend.user.recommend.strategy.PopularProductProjection;
import com.fasthub.backend.user.review.repository.ReviewRepository;
import com.fasthub.backend.user.review.repository.ReviewStatsProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProductService {

    private final UserProductRepository userProductRepository;
    private final ProductMapper productMapper;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    public Page<ResponseProductDto> search(String keyword, ProductCategory category, Long brandId, Integer minPrice, Integer maxPrice, String sortBy, Pageable pageable) {
        Page<Product> productPage = "popular".equals(sortBy)
                ? userProductRepository.searchProductsByPopularity(keyword, category, brandId, minPrice, maxPrice, pageable)
                : userProductRepository.searchProducts(keyword, category, brandId, minPrice, maxPrice, pageable);

        List<Long> ids = productPage.getContent().stream().map(Product::getId).toList();

        Map<Long, Long> reviewCountMap = reviewRepository.findReviewStatsByProductIds(ids).stream()
                .collect(Collectors.toMap(ReviewStatsProjection::getProductId, ReviewStatsProjection::getReviewCount));

        Map<Long, Long> orderCountMap = orderRepository.findOrderCountsByProductIds(ids).stream()
                .collect(Collectors.toMap(PopularProductProjection::getProductId, PopularProductProjection::getOrderCount));

        return productPage.map(p -> {
            ResponseProductDto dto = productMapper.productToProductDto(p);
            dto.setReviewCount(reviewCountMap.getOrDefault(p.getId(), 0L));
            dto.setOrderCount(orderCountMap.getOrDefault(p.getId(), 0L));
            return dto;
        });
    }

    public ResponseProductDto getDetail(Long id) {
        Product product = userProductRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_FAIL_SELECT));
        return productMapper.productToProductDto(product);
    }
}
