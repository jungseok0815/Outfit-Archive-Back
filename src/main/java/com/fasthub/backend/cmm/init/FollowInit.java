package com.fasthub.backend.cmm.init;

import com.fasthub.backend.user.follow.entity.Follow;
import com.fasthub.backend.user.follow.repository.FollowRepository;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(7)
public class FollowInit implements ApplicationRunner {

    private final FollowRepository followRepository;
    private final AuthRepository authRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (followRepository.count() > 0) {
            return;
        }

        List<User> users = authRepository.findAll();
        User user1 = users.get(0); // 홍길동
        User user2 = users.get(1); // 김영희
        User user3 = users.get(2); // 이철수

        // 홍길동 → 김영희 팔로우
        followRepository.save(Follow.builder()
                .follower(user1)
                .following(user2)
                .build());

        // 홍길동 → 이철수 팔로우
        followRepository.save(Follow.builder()
                .follower(user1)
                .following(user3)
                .build());

        // 김영희 → 홍길동 팔로우 (맞팔)
        followRepository.save(Follow.builder()
                .follower(user2)
                .following(user1)
                .build());

        // 이철수 → 홍길동 팔로우 (맞팔)
        followRepository.save(Follow.builder()
                .follower(user3)
                .following(user1)
                .build());

        // 이철수 → 김영희 팔로우
        followRepository.save(Follow.builder()
                .follower(user3)
                .following(user2)
                .build());

        log.info("[Init] Follow 초기 데이터 생성 완료 - 5건");
    }
}
