package com.fasthub.backend.user.notification.service;

import com.fasthub.backend.cmm.enums.NotificationType;
import com.fasthub.backend.user.notification.dto.NotificationResponseDto;
import com.fasthub.backend.user.notification.entity.Notification;
import com.fasthub.backend.user.notification.repository.NotificationRepository;
import com.fasthub.backend.user.usr.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterService sseEmitterService;

    // 알림 생성 + SSE 전송
    @Transactional
    public void send(User receiver, User sender, NotificationType type, String message, Long postId) {
        // 자기 자신에게는 알림 미발송
        if (receiver.getId().equals(sender.getId())) return;

        Notification notification = Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .type(type)
                .message(message)
                .postId(postId)
                .build();

        notificationRepository.save(notification);

        // 수신자가 현재 접속 중이면 SSE로 즉시 전송
        if (sseEmitterService.isConnected(receiver.getId())) {
            sseEmitterService.send(receiver.getId(), new NotificationResponseDto(notification));
        }
    }

    // 알림 목록 조회
    public List<NotificationResponseDto> list(Long userId) {
        return notificationRepository.findTop50ByReceiverIdOrderByCreatedAtDesc(userId)
                .stream().map(NotificationResponseDto::new).toList();
    }

    // 읽지 않은 알림 수
    public long countUnread(Long userId) {
        return notificationRepository.countByReceiverIdAndIsReadFalse(userId);
    }

    // 전체 읽음 처리
    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository.markAllReadByReceiverId(userId);
    }

    // 단건 읽음 처리
    @Transactional
    public void markRead(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            if (n.getReceiver().getId().equals(userId)) {
                n.markRead();
            }
        });
    }

    // 유저 삭제 시 연관 알림 삭제 (UserService에서 호출)
    @Transactional
    public void deleteByUser(Long userId) {
        notificationRepository.deleteByReceiverId(userId);
        notificationRepository.deleteBySenderId(userId);
    }

    // 읽지 않은 알림 수를 Map으로 반환 (SSE 연결 시 초기값)
    public Map<String, Long> unreadCountMap(Long userId) {
        return Map.of("unreadCount", countUnread(userId));
    }
}
