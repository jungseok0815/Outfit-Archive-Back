package com.fasthub.backend.user.follow.service;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.follow.dto.FollowCountDto;
import com.fasthub.backend.user.follow.dto.FollowUserDto;
import com.fasthub.backend.user.follow.entity.Follow;
import com.fasthub.backend.user.follow.repository.FollowRepository;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final AuthRepository authRepository;

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
        return followRepository.findFollowersByUserId(userId).stream()
                .map(u -> new FollowUserDto(u.getId(), u.getUserId(), u.getUserNm()))
                .toList();
    }

    public List<FollowUserDto> getFollowings(Long userId) {
        return followRepository.findFollowingsByUserId(userId).stream()
                .map(u -> new FollowUserDto(u.getId(), u.getUserId(), u.getUserNm()))
                .toList();
    }

    public FollowCountDto getFollowCount(Long userId) {
        return new FollowCountDto(
                followRepository.countByFollowingId(userId),
                followRepository.countByFollowerId(userId)
        );
    }
}
