package com.fasthub.backend.user.product.controller;

import com.fasthub.backend.admin.product.dto.ResponseProductDto;
import com.fasthub.backend.admin.product.service.ProductService;
import com.fasthub.backend.cmm.enums.ProductCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usr/product")
@RequiredArgsConstructor
public class UserProductController {

    private final ProductService productService;

    // 상품 목록 조회 (키워드 검색 + 카테고리 필터)
    @GetMapping("/list")
    public ResponseEntity<Page<ResponseProductDto>> list(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false) ProductCategory category,
            @PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(productService.listForUser(keyword, category, pageable));
    }
}
