package com.pavzar.stocktracker.service;

import com.pavzar.stocktracker.client.StockClient;
import com.pavzar.stocktracker.dto.AlphaVantageResponse;
import com.pavzar.stocktracker.dto.StockResponse;
import io.github.resilience4j.ratelimiter.RateLimiter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class StockQuoteService {
    private final StockClient stockClient;
    private final RateLimiter alpahaVantageRateLimiter;

    public StockQuoteService(StockClient stockClient, RateLimiter alpahaVantageRateLimiter) {
        this.stockClient = stockClient;
        this.alpahaVantageRateLimiter = alpahaVantageRateLimiter;
    }

    @Cacheable(value = "stocks", key = "#stockSymbol.trim().toUpperCase()")
    public StockResponse getStockForSymbol(String stockSymbol) {

        AlphaVantageResponse response = RateLimiter
                .decorateSupplier(
                        alpahaVantageRateLimiter,
                        () -> stockClient.getStockQuote(stockSymbol.trim().toUpperCase()))
                .get();

        if(response.globalQuote() == null || response.globalQuote().symbol() == null) {
            throw new RuntimeException("Invalid stock symbol: " + stockSymbol);
        }

        return StockResponse.builder()
                .symbol(response.globalQuote().symbol())
                .price(Double.parseDouble(response.globalQuote().price()))
                .lastUpdated(response.globalQuote().lastTradingDay())
                .build();
    }
}