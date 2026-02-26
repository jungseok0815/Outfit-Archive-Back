package com.fasthub.backend.user.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InsertCommentDto {

    @NotNull
    private Long postId;

    @NotBlank
    private String content;
}
