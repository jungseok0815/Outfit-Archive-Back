package com.fasthub.backend.cmm.schedule;

import com.fasthub.backend.cmm.naver.NaverProductCollectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Schedule {

    private final NaverProductCollectService naverProductCollectService;

    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")
    public void collectNaverProducts() {
        log.info("[Naver 수집 스케줄] 실행 시작 - 매일 새벽 2시");
        naverProductCollectService.collect();
        log.info("[Naver 수집 스케줄] 실행 완료");
    }

    public static void main(String[] args) {
    }
}
