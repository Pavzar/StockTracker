package com.pavzar.stocktracker.service;

import com.pavzar.stocktracker.client.StockClient;
import com.pavzar.stocktracker.dto.*;
import com.pavzar.stocktracker.entity.FavoriteStock;
import com.pavzar.stocktracker.exception.FavoriteStockAlreadyExistsException;
import com.pavzar.stocktracker.repository.FavoriteStockRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final StockClient stockClient;
    private final FavoriteStockRepository favoriteStockRepository;


    @Autowired
    public StockService(StockClient stockClient, FavoriteStockRepository favoriteStockRepository) {
        this.stockClient = stockClient;
        this.favoriteStockRepository = favoriteStockRepository;
    }

    public StockResponse getStockForSymbol(String stockSymbol) {
        AlphaVantageResponse response = stockClient.getStockQuote(stockSymbol);
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
}
