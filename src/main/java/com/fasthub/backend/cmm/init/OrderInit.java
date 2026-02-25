package com.fasthub.backend.cmm.init;

import com.fasthub.backend.admin.order.entity.Order;
import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.cmm.enums.OrderStatus;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@org.springframework.core.annotation.Order(5)
public class OrderInit implements ApplicationRunner {

    private final OrderRepository orderRepository;
    private final AuthRepository authRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (orderRepository.count() > 0) {
            return;
        }

        List<User> users       = authRepository.findAll();
        List<Product> products = productRepository.findAll();

        User user1 = users.get(0); // 홍길동
        User user2 = users.get(1); // 김영희
        User user3 = users.get(2); // 이철수

        Product airforce      = products.get(0); // 나이키 에어포스 1
        Product hoodie        = products.get(1); // 나이키 드라이핏 후디
        Product trainingPants = products.get(2); // 아디다스 트레이닝 팬츠
        Product jacket        = products.get(4); // 자라 오버핏 자켓

        orderRepository.save(Order.builder()
                .user(user1)
                .product(airforce)
                .quantity(1)
                .totalPrice(airforce.getProductPrice())
                .status(OrderStatus.PAYMENT_COMPLETE)
                .shippingAddress("서울 강남구 테헤란로 123")
                .recipientName("홍길동")
                .recipientPhone("010-1234-5678")
                .build());

        orderRepository.save(Order.builder()
                .user(user1)
                .product(trainingPants)
                .quantity(2)
                .totalPrice(trainingPants.getProductPrice() * 2)
                .status(OrderStatus.DELIVERED)
                .shippingAddress("서울 강남구 테헤란로 123")
                .recipientName("홍길동")
                .recipientPhone("010-1234-5678")
                .build());

        orderRepository.save(Order.builder()
                .user(user2)
                .product(jacket)
                .quantity(1)
                .totalPrice(jacket.getProductPrice())
                .status(OrderStatus.SHIPPING)
                .shippingAddress("부산 해운대구 해운대로 456")
                .recipientName("김영희")
                .recipientPhone("010-2345-6789")
                .build());

        orderRepository.save(Order.builder()
                .user(user3)
                .product(hoodie)
                .quantity(1)
                .totalPrice(hoodie.getProductPrice())
                .status(OrderStatus.PAYMENT_COMPLETE)
                .shippingAddress("대구 수성구 범어로 789")
                .recipientName("이철수")
                .recipientPhone("010-3456-7890")
                .build());

        log.info("[Init] Order 초기 데이터 생성 완료 - 4건");
    }
}
