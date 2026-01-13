package com.pavzar.stocktracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder, @Value("${alpha.vantage.base.url}") String baseUrl){
        return webClientBuilder.baseUrl(baseUrl).build();
    }
}
