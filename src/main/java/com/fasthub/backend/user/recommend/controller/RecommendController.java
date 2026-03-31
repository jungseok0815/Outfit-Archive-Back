package com.fasthub.backend.user.recommend.controller;

import com.fasthub.backend.user.recommend.dto.RecommendProductDto;
import com.fasthub.backend.user.recommend.service.RecommendService;
import com.fasthub.backend.user.usr.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
@Slf4j
public class RecommendController {

    private final RecommendService recommendService;

    // 비로그인 → 인기 상품 / 로그인 → 조회 기반 추천
    @GetMapping("/products")
    public ResponseEntity<List<RecommendProductDto>> recommendProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = (userDetails != null) ? userDetails.getId() : null;
        return ResponseEntity.ok(recommendService.recommend(userId, limit));
    }

    // AI 추천 버튼 클릭 시 벡터 기반 추천 (로그인 필요)
    @GetMapping("/ai")
    public ResponseEntity<List<RecommendProductDto>> recommendAi(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("come in ai recomand");
        Long userId = (userDetails != null) ? userDetails.getId() : null;
        return ResponseEntity.ok(recommendService.recommendAi(userId, limit));
    }

    // 로그인 여부 무관하게 항상 인기 상품 반환
    @GetMapping("/popular")
    public ResponseEntity<List<RecommendProductDto>> popularProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendService.recommendPopular(limit));
    }
}
