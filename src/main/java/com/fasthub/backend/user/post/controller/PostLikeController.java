package com.fasthub.backend.user.post.controller;

import com.fasthub.backend.user.post.service.PostLikeService;
import com.fasthub.backend.user.usr.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usr/post/like")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    // 좋아요 토글 (좋아요/취소)
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggle(
            @RequestParam Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean liked = postLikeService.toggle(postId, userDetails.getId());
        long likeCount = postLikeService.count(postId);
        return ResponseEntity.ok(Map.of("liked", liked, "likeCount", likeCount));
    }

    // 좋아요 상태 조회
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status(
            @RequestParam Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean liked = postLikeService.isLiked(postId, userDetails.getId());
        long likeCount = postLikeService.count(postId);
        return ResponseEntity.ok(Map.of("liked", liked, "likeCount", likeCount));
    }
}
