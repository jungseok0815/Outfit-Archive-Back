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

    // 비로그인 사용자도 접근 가능 (SecurityConfig에서 permitAll 설정)
    // 로그인 사용자는 구매 이력 기반으로 분기, 비로그인은 인기 상품 반환
    @GetMapping("/products")
    public ResponseEntity<List<RecommendProductDto>> recommendProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = (userDetails != null) ? userDetails.getId() : null;
        return ResponseEntity.ok(recommendService.recommend(userId, limit));
    }

    // 로그인 여부 무관하게 항상 인기 상품 반환
    @GetMapping("/popular")
    public ResponseEntity<List<RecommendProductDto>> popularProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendService.recommendPopular(limit));
    }
}
