package com.fasthub.backend.cmm.naver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NaverProductCollectServiceTest {

    @Autowired
    private NaverProductCollectService naverProductCollectService;

    @Test
    void collectTest() {
        naverProductCollectService.collect();
    }
}
