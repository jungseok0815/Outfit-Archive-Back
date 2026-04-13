package com.fasthub.backend.user.follow.service;

import com.fasthub.backend.cmm.enums.NotificationType;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.follow.dto.FollowCountDto;
import com.fasthub.backend.user.follow.dto.FollowUserDto;
import com.fasthub.backend.user.follow.entity.Follow;
import com.fasthub.backend.user.follow.repository.FollowRepository;
import com.fasthub.backend.user.notification.service.NotificationService;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final AuthRepository authRepository;
    private final NotificationService notificationService;

    @Transactional
    public void follow(Long followerId, Long targetId) {
        if (followerId.equals(targetId)) {
            throw new BusinessException(ErrorCode.FOLLOW_SELF);
        }
        User follower = authRepository.findById(followerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        User following = authRepository.findById(targetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new BusinessException(ErrorCode.FOLLOW_ALREADY_EXISTS);
        }
        followRepository.save(Follow.builder()
                .follower(follower)
                .following(following)
                .build());

        notificationService.send(following, follower, NotificationType.FOLLOW,
                follower.getUserNm() + "님이 팔로우했습니다.", null);
    }

    @Transactional
    public void unfollow(Long followerId, Long targetId) {
        User follower = authRepository.findById(followerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        User following = authRepository.findById(targetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new BusinessException(ErrorCode.FOLLOW_NOT_FOUND));
        followRepository.delete(follow);
    }

    public List<FollowUserDto> getFollowers(Long userId) {
        List<FollowUserDto> followers = followRepository.findFollowersByUserId(userId).stream()
                .map(u -> new FollowUserDto(u.getId(), u.getUserId(), u.getUserNm(), u.getProfileImgNm()))
                .toList();
        log.info("[Follow] 팔로워 목록 userId={}, count={}", userId, followers.size());
        followers.forEach(f -> log.info("[Follow] 팔로워 id={}, userId={}, userNm={}, profileImgNm={}",
                f.getId(), f.getUserId(), f.getUserNm(), f.getProfileImgNm()));
        return followers;
    }

    public List<FollowUserDto> getFollowings(Long userId) {
        List<FollowUserDto> followings = followRepository.findFollowingsByUserId(userId).stream()
                .map(u -> new FollowUserDto(u.getId(), u.getUserId(), u.getUserNm(), u.getProfileImgNm()))
                .toList();
        log.info("[Follow] 팔로잉 목록 userId={}, count={}", userId, followings.size());
        followings.forEach(f -> log.info("[Follow] 팔로잉 id={}, userId={}, userNm={}, profileImgNm={}",
                f.getId(), f.getUserId(), f.getUserNm(), f.getProfileImgNm()));
        return followings;
    }

    public boolean isFollowing(Long followerId, Long targetId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, targetId);
    }

    public FollowCountDto getFollowCount(Long userId) {
        return new FollowCountDto(
                followRepository.countByFollowingId(userId),
                followRepository.countByFollowerId(userId)
        );
    }
}
