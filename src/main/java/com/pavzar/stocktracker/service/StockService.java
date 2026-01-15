package com.pavzar.stocktracker.service;

import com.pavzar.stocktracker.client.StockClient;
import com.pavzar.stocktracker.dto.DailyStockResponse;
import com.pavzar.stocktracker.dto.StockHistoryResponse;
import com.pavzar.stocktracker.dto.StockOverviewResponse;
import com.pavzar.stocktracker.dto.StockResponse;
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
    private final StockQuoteService stockQuoteService;

    @Autowired
    public StockService(StockClient stockClient, FavoriteStockRepository favoriteStockRepository, StockQuoteService stockQuoteService) {
        this.stockClient = stockClient;
        this.favoriteStockRepository = favoriteStockRepository;
        this.stockQuoteService = stockQuoteService;
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
                .map(FavoriteStock::getSymbol)
                .map(stockQuoteService::getStockForSymbol)
                .toList();
    }
}
