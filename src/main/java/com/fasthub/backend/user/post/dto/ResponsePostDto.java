package com.fasthub.backend.user.post.dto;

import com.fasthub.backend.user.post.entity.Post;
import com.fasthub.backend.user.post.entity.PostImg;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ResponsePostDto {

    private Long id;
    private String title;
    private String content;
    private String userNm;
    private List<PostImg> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ResponsePostDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.userNm = post.getUser().getUserNm();
        this.images = post.getImages();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}
