package com.fasthub.backend.user.productview.controller;

import com.fasthub.backend.user.productview.service.ProductViewService;
import com.fasthub.backend.user.usr.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usr/product/view")
@RequiredArgsConstructor
public class ProductViewController {

    private final ProductViewService productViewService;

    // 상품 조회 기록
    @PostMapping("/{productId}")
    public ResponseEntity<Void> recordView(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        productViewService.recordView(userDetails.getId(), productId);
        return ResponseEntity.ok().build();
    }
}
