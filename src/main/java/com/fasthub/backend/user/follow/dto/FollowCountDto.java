package com.fasthub.backend.user.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowCountDto {
    private long followerCount;
    private long followingCount;
}
