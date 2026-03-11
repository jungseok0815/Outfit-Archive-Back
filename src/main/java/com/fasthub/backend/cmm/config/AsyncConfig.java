package com.fasthub.backend.cmm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

// @Configuration  // AI 임베딩 기능 비활성화
// @EnableAsync
public class AsyncConfig {

//    @Bean(name = "embeddingExecutor")
//    public Executor embeddingExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(2);
//        executor.setMaxPoolSize(5);
//        executor.setQueueCapacity(100);
//        executor.setThreadNamePrefix("embedding-");
//        executor.initialize();
//        return executor;
//    }
}
