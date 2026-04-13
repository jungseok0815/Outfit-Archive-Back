package com.fasthub.backend.user.notification.repository;

import com.fasthub.backend.user.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 수신자 기준 최신순 조회 (최대 50개)
    List<Notification> findTop50ByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    // 읽지 않은 알림 수
    long countByReceiverIdAndIsReadFalse(Long receiverId);

    // 수신자의 모든 알림 일괄 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.receiver.id = :receiverId AND n.isRead = false")
    void markAllReadByReceiverId(@Param("receiverId") Long receiverId);

    // 유저 삭제 시 연관 알림 삭제
    void deleteByReceiverId(Long receiverId);
    void deleteBySenderId(Long senderId);
}
