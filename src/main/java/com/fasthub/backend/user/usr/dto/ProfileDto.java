package com.fasthub.backend.user.usr.dto;

import com.fasthub.backend.user.usr.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileDto {
    private Long id;
    private String userId;
    private String userNm;
    private int userAge;
    private String bio;
    private String profileImgNm;
    private int point;
    private long followerCount;
    private long followingCount;
    private long postCount;

    public static ProfileDto of(User user, long followerCount, long followingCount, long postCount) {
        return ProfileDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .userNm(user.getUserNm())
                .userAge(user.getUserAge())
                .bio(user.getBio())
                .profileImgNm(user.getProfileImgNm())
                .point(user.getPoint())
                .followerCount(followerCount)
                .followingCount(followingCount)
                .postCount(postCount)
                .build();
    }
}
