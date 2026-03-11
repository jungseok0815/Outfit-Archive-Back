package com.fasthub.backend.cmm.init;

import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.user.point.entity.PointHistory;
import com.fasthub.backend.user.point.entity.PointHistory.PointType;
import com.fasthub.backend.user.point.repository.PointHistoryRepository;
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
@Order(8)
public class PointInit implements ApplicationRunner {

    private final PointHistoryRepository pointHistoryRepository;
    private final AuthRepository authRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (pointHistoryRepository.count() > 0) {
            return;
        }

        List<User> users = authRepository.findAll();
        List<Product> products = productRepository.findAll();

        User user1 = users.get(0); // 홍길동
        User user2 = users.get(1); // 김영희
        User user3 = users.get(2); // 이철수

        Product airforce      = products.get(0); // 나이키 에어포스 1
        Product hoodie        = products.get(1); // 나이키 드라이핏 후디
        Product trainingPants = products.get(2); // 아디다스 트레이닝 팬츠
        Product jacket        = products.get(4); // 자라 오버핏 자켓

        // 홍길동: 에어포스 구매 적립 (119,000 * 1% = 1,190)
        user1.earnPoint(1190);
        pointHistoryRepository.save(PointHistory.builder()
                .user(user1).amount(1190).balanceAfter(1190)
                .description("구매 적립 - " + airforce.getProductNm())
                .type(PointType.EARN).build());

        // 홍길동: 트레이닝 팬츠 2개 구매 적립 (178,000 * 1% = 1,780)
        user1.earnPoint(1780);
        pointHistoryRepository.save(PointHistory.builder()
                .user(user1).amount(1780).balanceAfter(2970)
                .description("구매 적립 - " + trainingPants.getProductNm())
                .type(PointType.EARN).build());

        // 홍길동: 포인트 500 사용
        user1.usePoint(500);
        pointHistoryRepository.save(PointHistory.builder()
                .user(user1).amount(-500).balanceAfter(2470)
                .description("포인트 사용 - " + hoodie.getProductNm())
                .type(PointType.USE).build());

        // 김영희: 자켓 구매 적립 (159,000 * 1% = 1,590)
        user2.earnPoint(1590);
        pointHistoryRepository.save(PointHistory.builder()
                .user(user2).amount(1590).balanceAfter(1590)
                .description("구매 적립 - " + jacket.getProductNm())
                .type(PointType.EARN).build());

        // 김영희: 포인트 300 사용
        user2.usePoint(300);
        pointHistoryRepository.save(PointHistory.builder()
                .user(user2).amount(-300).balanceAfter(1290)
                .description("포인트 사용 - " + airforce.getProductNm())
                .type(PointType.USE).build());

        // 이철수: 후디 구매 적립 (99,000 * 1% = 990)
        user3.earnPoint(990);
        pointHistoryRepository.save(PointHistory.builder()
                .user(user3).amount(990).balanceAfter(990)
                .description("구매 적립 - " + hoodie.getProductNm())
                .type(PointType.EARN).build());

        authRepository.saveAll(List.of(user1, user2, user3));

        log.info("[Init] PointHistory 초기 데이터 생성 완료 - 6건 (홍길동: 2470P, 김영희: 1290P, 이철수: 990P)");
    }
}
