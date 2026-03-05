package com.fasthub.backend.user.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowUserDto {
    private Long id;
    private String userId;
    private String userNm;
}
