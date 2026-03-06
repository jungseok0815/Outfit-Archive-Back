package com.fasthub.backend.user.point.controller;

import com.fasthub.backend.user.point.dto.PointHistoryDto;
import com.fasthub.backend.user.point.service.PointService;
import com.fasthub.backend.user.usr.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/usr/point")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    // 포인트 잔액 조회
    @GetMapping
    public ResponseEntity<Map<String, Integer>> getPoint(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(Map.of("point", pointService.getPoint(userDetails.getId())));
    }

    // 포인트 내역 조회
    @GetMapping("/history")
    public ResponseEntity<Page<PointHistoryDto>> getHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(pointService.getHistory(userDetails.getId(), pageable));
    }
}
