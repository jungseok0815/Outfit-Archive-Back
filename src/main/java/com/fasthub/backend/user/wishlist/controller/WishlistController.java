package com.fasthub.backend.user.wishlist.controller;

import com.fasthub.backend.user.usr.dto.CustomUserDetails;
import com.fasthub.backend.user.wishlist.dto.WishlistResponseDto;
import com.fasthub.backend.user.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usr/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    // 관심상품 토글 (추가/제거)
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Boolean>> toggle(
            @RequestParam Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean wished = wishlistService.toggle(productId, userDetails.getId());
        return ResponseEntity.ok(Map.of("wished", wished));
    }

    // 관심상품 여부 확인
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> status(
            @RequestParam Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean wished = wishlistService.isWished(productId, userDetails.getId());
        return ResponseEntity.ok(Map.of("wished", wished));
    }

    // 나의 관심상품 목록
    @GetMapping
    public ResponseEntity<Page<WishlistResponseDto>> myWishlist(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        return ResponseEntity.ok(wishlistService.myWishlist(userDetails.getId(), pageable));
    }
}
