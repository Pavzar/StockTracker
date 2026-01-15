package com.pavzar.stocktracker.service;

import com.pavzar.stocktracker.client.StockClient;
import com.pavzar.stocktracker.dto.*;
import com.pavzar.stocktracker.entity.FavoriteStock;
import com.pavzar.stocktracker.exception.FavoriteStockAlreadyExistsException;
import com.pavzar.stocktracker.repository.FavoriteStockRepository;
import io.github.resilience4j.ratelimiter.RateLimiter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StockService {

    private final StockClient stockClient;
    private final FavoriteStockRepository favoriteStockRepository;
    private final RateLimiter alpahaVantageRateLimiter;


    private final StockService self;

    @Autowired
    public StockService(StockClient stockClient, FavoriteStockRepository favoriteStockRepository, RateLimiter alpahaVantageRateLimiter, @Lazy StockService self) {
        this.stockClient = stockClient;
        this.favoriteStockRepository = favoriteStockRepository;
        this.alpahaVantageRateLimiter = alpahaVantageRateLimiter;
        this.self = self;
    }

    @Cacheable(value = "stocks", key = "#stockSymbol.trim().toUpperCase()")
    public StockResponse getStockForSymbol(String stockSymbol) {

        AlphaVantageResponse response = RateLimiter
                .decorateSupplier(
                        alpahaVantageRateLimiter,
                        () -> stockClient.getStockQuote(stockSymbol.trim().toUpperCase()))
                .get();


        if(response.globalQuote() == null || response.globalQuote().symbol() == null) {
            System.out.println(response);
            throw new RuntimeException("Invalid stock symbol: " + stockSymbol);
        }

        return StockResponse.builder()
                .symbol(response.globalQuote().symbol())
                .price(Double.parseDouble(response.globalQuote().price()))
                .lastUpdated(response.globalQuote().lastTradingDay())
                .build();
    }

    public StockOverviewResponse getStockOverviewForSymbol(String symbol) {
        return stockClient.getStockOverview(symbol);
    }

    public List<DailyStockResponse> getHistory(String symbol, int days) {
        StockHistoryResponse response = stockClient.getStockHistory(symbol);

        return response.timeSeries().entrySet().stream()
                .limit(days)
                .map(entry -> new DailyStockResponse(
                        entry.getKey(),
                        Double.parseDouble(entry.getValue().open()),
                        Double.parseDouble(entry.getValue().high()),
                        Double.parseDouble(entry.getValue().low()),
                        Double.parseDouble(entry.getValue().close()),
                        Long.parseLong(entry.getValue().volume())
                ))
                .toList();
    }

    @Transactional
    public FavoriteStock saveFavorite(String symbol) {
        if (favoriteStockRepository.existsBySymbol(symbol)) {
            throw new FavoriteStockAlreadyExistsException(symbol);
        }
        FavoriteStock favoriteStock = FavoriteStock.builder().symbol(symbol).build();

        return favoriteStockRepository.save(favoriteStock);
    }

    public List<StockResponse> getFavoritesWithLivePrices() {
        List<FavoriteStock> favoriteStocks = favoriteStockRepository.findAll();

        return favoriteStocks.stream()
                .map(favStock -> self.getStockForSymbol(favStock.getSymbol()))
                .toList();
    }
}
