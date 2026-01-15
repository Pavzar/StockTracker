package com.pavzar.stocktracker.service;

import com.pavzar.stocktracker.client.StockClient;
import com.pavzar.stocktracker.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final StockClient stockClient;

    @Autowired
    public StockService(StockClient stockClient) {
        this.stockClient = stockClient;
    }

    public StockResponse getStockForSymbol(String stockSymbol){
        AlphaVantageResponse response = stockClient.getStockQuote(stockSymbol);
        return StockResponse.builder()
                .symbol(response.globalQuote().symbol())
                .price(Double.parseDouble(response.globalQuote().price()))
                .lastUpdated(response.globalQuote().lastTradingDay())
                .build();
    }

    public StockOverviewResponse getStockOverviewForSymbol(String symbol){
        return stockClient.getStockOverview(symbol);
    }

    public List<DailyStockResponse> getHistory(String symbol, int days){
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
}
