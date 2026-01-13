package com.pavzar.stocktracker.service;

import com.pavzar.stocktracker.client.StockClient;
import com.pavzar.stocktracker.dto.AlphaVantageResponse;
import com.pavzar.stocktracker.dto.StockOverviewResponse;
import com.pavzar.stocktracker.dto.StockResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
