package com.pavzar.stocktracker.client;

import com.pavzar.stocktracker.dto.AlphaVantageResponse;
import com.pavzar.stocktracker.dto.StockHistoryResponse;
import com.pavzar.stocktracker.dto.StockOverviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class StockClient {

    private final WebClient webClient;

    @Value("${alpha.vantage.api.key}")
    private String apiKey;

    public AlphaVantageResponse getStockQuote(String symbol){
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .queryParam("function", "GLOBAL_QUOTE")
                        .queryParam("symbol", symbol)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(AlphaVantageResponse.class)
                .block();
    }

    public StockOverviewResponse getStockOverview(String symbol){
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .queryParam("function", "OVERVIEW")
                        .queryParam("symbol", symbol)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(StockOverviewResponse.class)
                .block();
    }

    public StockHistoryResponse getStockHistory(String symbol){
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .queryParam("function","TIME_SERIES_DAILY")
                        .queryParam("symbol",symbol)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(StockHistoryResponse.class)
                .block();
    }
}
