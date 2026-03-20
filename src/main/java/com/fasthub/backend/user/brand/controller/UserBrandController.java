package com.fasthub.backend.user.brand.controller;

import com.fasthub.backend.admin.brand.dto.ResponseBrandDto;
import com.fasthub.backend.admin.brand.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usr/brand")
@RequiredArgsConstructor
public class UserBrandController {

    private final BrandService brandService;

    // 브랜드 목록 조회 (키워드 검색)
    @GetMapping("/list")
    public ResponseEntity<Page<ResponseBrandDto>> list(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(brandService.list(keyword, pageable));
    }

    // 브랜드 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ResponseBrandDto> detail(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.getById(id));
    }
}
