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
@RequestMapping("/api/usr/recommend")
@RequiredArgsConstructor
@Slf4j
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("/products")
    public ResponseEntity<List<RecommendProductDto>> recommendProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendService.recommend(userDetails.getId(), limit));
    }
}
