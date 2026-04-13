package com.fasthub.backend.user.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 접속 중인 유저의 SSE Emitter 관리
@Service
@Slf4j
public class SseEmitterService {

    private static final long SSE_TIMEOUT = 30 * 60 * 1000L; // 30분
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        emitters.put(userId, emitter);

        // 연결 확인용 초기 이벤트 (503 방지)
        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    public void send(Long userId, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;
        try {
            emitter.send(SseEmitter.event().name("notification").data(data));
        } catch (IOException e) {
            emitters.remove(userId);
            log.warn("SSE 전송 실패 userId={}", userId);
        }
    }

    public boolean isConnected(Long userId) {
        return emitters.containsKey(userId);
    }
}
