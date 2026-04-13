package com.fasthub.backend.user.notification.dto;

import com.fasthub.backend.user.notification.entity.Notification;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponseDto {

    private final Long id;
    private final String type;
    private final String message;
    private final Long postId;
    private final boolean read;
    private final LocalDateTime createdAt;
    private final String senderNm;
    private final String senderProfileImg;

    public NotificationResponseDto(Notification n) {
        this.id = n.getId();
        this.type = n.getType().name();
        this.message = n.getMessage();
        this.postId = n.getPostId();
        this.read = n.isRead();
        this.createdAt = n.getCreatedAt();
        this.senderNm = n.getSender().getUserNm();
        this.senderProfileImg = n.getSender().getProfileImgNm();
    }
}
