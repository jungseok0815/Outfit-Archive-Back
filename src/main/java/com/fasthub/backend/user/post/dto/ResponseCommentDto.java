package com.fasthub.backend.user.post.dto;

import com.fasthub.backend.user.post.entity.PostComment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ResponseCommentDto {

    private Long id;
    private Long postId;
    private Long userId;
    private String userNm;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ResponseCommentDto(PostComment comment) {
        this.id = comment.getId();
        this.postId = comment.getPost().getId();
        this.userId = comment.getUser().getId();
        this.userNm = comment.getUser().getUserNm();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}
