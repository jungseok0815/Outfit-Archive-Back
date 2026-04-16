package com.fasthub.backend.cmm.naver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "jasypt.encryptor.password=")
class NaverProductCollectServiceTest {

    @Autowired
    private NaverProductCollectService naverProductCollectService;

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    @Test
    void checkNaverKeys() {
        System.out.println("clientId: " + clientId);
        System.out.println("clientSecret: " + clientSecret);
    }

    @Test
    void collectTest() {
        naverProductCollectService.collect();
    }
}
