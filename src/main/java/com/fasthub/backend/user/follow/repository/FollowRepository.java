package com.fasthub.backend.user.follow.repository;

import com.fasthub.backend.user.follow.entity.Follow;
import com.fasthub.backend.user.usr.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 팔로우 존재 여부 확인
    boolean existsByFollowerAndFollowing(User follower, User following);

    // ID로 팔로우 여부 확인 (상태 조회용)
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // 팔로우 관계 조회 (언팔로우 시 사용)
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    // 특정 유저를 팔로우하는 사람들 (팔로워 목록)
    @Query("SELECT u FROM Follow f JOIN f.follower u WHERE f.following.id = :userId")
    List<User> findFollowersByUserId(@Param("userId") Long userId);

    // 특정 유저가 팔로우하는 사람들 (팔로잉 목록)
    @Query("SELECT u FROM Follow f JOIN f.following u WHERE f.follower.id = :userId")
    List<User> findFollowingsByUserId(@Param("userId") Long userId);

    // 팔로워 수
    long countByFollowingId(Long followingId);

    // 팔로잉 수
    long countByFollowerId(Long followerId);
}
