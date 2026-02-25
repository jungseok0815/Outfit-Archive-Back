package com.fasthub.backend.cmm.init;

import com.fasthub.backend.user.post.entity.Post;
import com.fasthub.backend.user.post.repository.PostRepository;
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
@Order(6)
public class PostInit implements ApplicationRunner {

    private final PostRepository postRepository;
    private final AuthRepository authRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (postRepository.count() > 0) {
            return;
        }

        List<User> users = authRepository.findAll();
        User user1 = users.get(0); // 홍길동
        User user2 = users.get(1); // 김영희
        User user3 = users.get(2); // 이철수

        postRepository.save(Post.builder()
                .title("오늘의 코디 - 캐주얼 룩")
                .content("나이키 에어포스에 청바지 조합! 가볍고 편안한 데일리 룩입니다.")
                .user(user1)
                .build());

        postRepository.save(Post.builder()
                .title("자라 오버핏 자켓 리뷰")
                .content("자라 오버핏 자켓 구매 후기입니다. 핏이 정말 예쁘고 소재도 좋아요.")
                .user(user2)
                .build());

        postRepository.save(Post.builder()
                .title("아디다스 트레이닝 세트 코디")
                .content("아디다스 트레이닝 팬츠 + 후디 세트 코디. 운동할 때도, 데일리로도 최고!")
                .user(user3)
                .build());

        postRepository.save(Post.builder()
                .title("겨울 아우터 추천")
                .content("요즘 날씨에 딱 맞는 오버핏 자켓 추천드립니다. 레이어드하기도 좋아요.")
                .user(user1)
                .build());

        log.info("[Init] Post 초기 데이터 생성 완료 - 4건");
    }
}
