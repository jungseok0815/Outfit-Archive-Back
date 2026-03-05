package com.fasthub.backend.user.follow.controller;

import com.fasthub.backend.user.follow.dto.FollowCountDto;
import com.fasthub.backend.user.follow.dto.FollowUserDto;
import com.fasthub.backend.user.follow.service.FollowService;
import com.fasthub.backend.user.usr.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usr/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    // 팔로우
    @PostMapping("/{targetId}")
    public ResponseEntity<Void> follow(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long targetId) {
        followService.follow(userDetails.getId(), targetId);
        return ResponseEntity.ok().build();
    }

    // 언팔로우
    @DeleteMapping("/{targetId}")
    public ResponseEntity<Void> unfollow(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long targetId) {
        followService.unfollow(userDetails.getId(), targetId);
        return ResponseEntity.noContent().build();
    }

    // 팔로워 목록 (나를 팔로우하는 사람들)
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<FollowUserDto>> followers(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowers(userId));
    }

    // 팔로잉 목록 (내가 팔로우하는 사람들)
    @GetMapping("/{userId}/followings")
    public ResponseEntity<List<FollowUserDto>> followings(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowings(userId));
    }

    // 팔로워/팔로잉 수 조회
    @GetMapping("/{userId}/count")
    public ResponseEntity<FollowCountDto> count(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowCount(userId));
    }
}
