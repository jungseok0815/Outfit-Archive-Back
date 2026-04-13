package com.fasthub.backend.user.notification.controller;

import com.fasthub.backend.user.notification.dto.NotificationResponseDto;
import com.fasthub.backend.user.notification.service.NotificationService;
import com.fasthub.backend.user.notification.service.SseEmitterService;
import com.fasthub.backend.user.usr.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usr/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SseEmitterService sseEmitterService;

    // SSE 구독 (로그인 후 프론트에서 한 번 연결)
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return sseEmitterService.subscribe(userDetails.getId());
    }

    // 알림 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<NotificationResponseDto>> list(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(notificationService.list(userDetails.getId()));
    }

    // 읽지 않은 알림 수
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(notificationService.unreadCountMap(userDetails.getId()));
    }

    // 전체 읽음 처리
    @PatchMapping("/read-all")
    public ResponseEntity<Void> readAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.markAllRead(userDetails.getId());
        return ResponseEntity.ok().build();
    }

    // 단건 읽음 처리
    @PatchMapping("/read/{id}")
    public ResponseEntity<Void> readOne(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.markRead(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }
}
