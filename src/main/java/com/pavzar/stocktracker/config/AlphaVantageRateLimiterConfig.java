package com.pavzar.stocktracker.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AlphaVantageRateLimiterConfig {

    @Bean
    public RateLimiter alphaVantageRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(1)
                .limitRefreshPeriod(Duration.ofMillis(4000))
                .timeoutDuration(Duration.ofSeconds(30))
                .build();

        return RateLimiter.of("alphaVantageRateLimiter", config);
    }
}
