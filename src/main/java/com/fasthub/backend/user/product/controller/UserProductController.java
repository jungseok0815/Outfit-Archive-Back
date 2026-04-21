package com.fasthub.backend.user.product.controller;

import com.fasthub.backend.admin.product.dto.ResponseProductDto;
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

    @GetMapping("/list")
    public ResponseEntity<Page<ResponseProductDto>> list(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false, defaultValue = "popular") String sortBy,
            @PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userProductService.search(keyword, categoryId, brandId, minPrice, maxPrice, sortBy, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseProductDto> detail(@PathVariable Long id) {
        return ResponseEntity.ok(userProductService.getDetail(id));
    }
}
