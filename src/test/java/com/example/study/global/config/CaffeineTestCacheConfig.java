package com.example.study.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

@TestConfiguration
@EnableCaching
public class CaffeineTestCacheConfig {

    @Bean
    public CaffeineCacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // Caffeine 캐시의 세부 설정 (만료 시간, 최대 크기 등)
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES) // 10분 후 만료
                .maximumSize(1_000) // 최대 1,000개까지만 저장 (메모리 보호)
        );

        return cacheManager;
    }
}
