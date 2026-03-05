package com.fasthub.backend.user.product.controller;

import com.fasthub.backend.admin.product.dto.ResponseProductDto;
import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.user.product.service.UserProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usr/product")
@RequiredArgsConstructor
public class UserProductController {

    private final UserProductService userProductService;

    // 상품 목록 조회 (키워드 검색 + 카테고리 / 브랜드 / 가격 범위 필터)
    @GetMapping("/list")
    public ResponseEntity<Page<ResponseProductDto>> list(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userProductService.search(keyword, category, brandId, minPrice, maxPrice, pageable));
    }

    // 상품 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ResponseProductDto> detail(@PathVariable Long id) {
        return ResponseEntity.ok(userProductService.getDetail(id));
    }
}
