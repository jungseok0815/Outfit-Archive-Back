package com.fasthub.backend.user.similar.controller;

import com.fasthub.backend.user.similar.dto.SimilarProductDto;
import com.fasthub.backend.user.similar.service.SimilarProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController  // AI 유사 상품 기능 비활성화
// @RequestMapping("/api/usr/product")
@RequiredArgsConstructor
public class SimilarProductController {

    private final SimilarProductService similarProductService;

    @GetMapping("/similar")
    public ResponseEntity<List<SimilarProductDto>> similar(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(similarProductService.findSimilar(productId, limit));
    }
}
