package com.fasthub.backend.user.notification;

import com.fasthub.backend.cmm.enums.NotificationType;
import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.user.notification.entity.Notification;
import com.fasthub.backend.user.notification.repository.NotificationRepository;
import com.fasthub.backend.user.notification.service.NotificationService;
import com.fasthub.backend.user.notification.service.SseEmitterService;
import com.fasthub.backend.user.usr.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService 테스트")
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SseEmitterService sseEmitterService;

    private User buildUser(Long id) {
        return User.builder()
                .id(id)
                .userId("user" + id)
                .userNm("사용자" + id)
                .userPwd("encodedPwd")
                .userAge(25)
                .authName(UserRole.ROLE_USER)
                .build();
    }

    // ────────────────────────────────────────────────
    // 알림 전송
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("알림 전송")
    class Send {

        @Test
        @DisplayName("성공 - 수신자와 발신자가 다르면 알림 저장")
        void send_success() {
            User receiver = buildUser(1L);
            User sender = buildUser(2L);

            given(sseEmitterService.isConnected(1L)).willReturn(false);

            notificationService.send(receiver, sender, NotificationType.LIKE, "좋아요", 1L);

            then(notificationRepository).should().save(any(Notification.class));
        }

        @Test
        @DisplayName("성공 - 수신자가 접속 중이면 SSE 전송")
        void send_success_withSse() {
            User receiver = buildUser(1L);
            User sender = buildUser(2L);

            given(sseEmitterService.isConnected(1L)).willReturn(true);

            notificationService.send(receiver, sender, NotificationType.LIKE, "좋아요", 1L);

            then(notificationRepository).should().save(any(Notification.class));
            then(sseEmitterService).should().send(any(), any());
        }

        @Test
        @DisplayName("스킵 - 자기 자신에게는 알림 미발송")
        void send_skip_selfNotification() {
            User user = buildUser(1L);

            notificationService.send(user, user, NotificationType.LIKE, "좋아요", 1L);

            then(notificationRepository).should(never()).save(any());
        }
    }

    // ────────────────────────────────────────────────
    // 읽음 처리
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("읽음 처리")
    class MarkRead {

        @Test
        @DisplayName("성공 - 본인 알림이면 읽음 처리")
        void markRead_success() {
            User receiver = buildUser(1L);
            Notification notification = Notification.builder()
                    .receiver(receiver)
                    .sender(buildUser(2L))
                    .type(NotificationType.LIKE)
                    .message("좋아요")
                    .build();

            given(notificationRepository.findById(1L)).willReturn(Optional.of(notification));

            notificationService.markRead(1L, 1L);

            assertThat(notification.isRead()).isTrue();
        }

        @Test
        @DisplayName("스킵 - 본인 알림이 아니면 읽음 처리 안 함")
        void markRead_skip_notOwner() {
            User receiver = buildUser(1L);
            Notification notification = Notification.builder()
                    .receiver(receiver)
                    .sender(buildUser(2L))
                    .type(NotificationType.LIKE)
                    .message("좋아요")
                    .build();

            given(notificationRepository.findById(1L)).willReturn(Optional.of(notification));

            notificationService.markRead(1L, 999L);

            assertThat(notification.isRead()).isFalse();
        }
    }

    // ────────────────────────────────────────────────
    // 읽지 않은 알림 수
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("읽지 않은 알림 수")
    class CountUnread {

        @Test
        @DisplayName("성공")
        void countUnread_success() {
            given(notificationRepository.countByReceiverIdAndIsReadFalse(1L)).willReturn(3L);

            long result = notificationService.countUnread(1L);

            assertThat(result).isEqualTo(3L);
        }
    }

    // ────────────────────────────────────────────────
    // 유저 알림 삭제
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("유저 알림 삭제")
    class DeleteByUser {

        @Test
        @DisplayName("성공 - 수신/발신 알림 전부 삭제")
        void deleteByUser_success() {
            notificationService.deleteByUser(1L);

            then(notificationRepository).should().deleteByReceiverId(1L);
            then(notificationRepository).should().deleteBySenderId(1L);
        }
    }

    // ────────────────────────────────────────────────
    // 알림 목록 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("알림 목록 조회")
    class GetList {

        @Test
        @DisplayName("성공")
        void list_success() {
            given(notificationRepository.findTop50ByReceiverIdOrderByCreatedAtDesc(1L))
                    .willReturn(List.of());

            var result = notificationService.list(1L);

            assertThat(result).isEmpty();
        }
    }
}
