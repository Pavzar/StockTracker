package com.pavzar.stocktracker.controller;

import com.pavzar.stocktracker.dto.DailyStockResponse;
import com.pavzar.stocktracker.dto.FavoriteStockRequest;
import com.pavzar.stocktracker.dto.StockOverviewResponse;
import com.pavzar.stocktracker.dto.StockResponse;
import com.pavzar.stocktracker.entity.FavoriteStock;
import com.pavzar.stocktracker.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/{stockSymbol}")
    public StockResponse getStock(@PathVariable String stockSymbol){
        return stockService.getStockForSymbol(stockSymbol.toUpperCase());
    }

    @GetMapping("{stockSymbol}/overview")
    public StockOverviewResponse getStockOverview(@PathVariable String stockSymbol){
        return stockService.getStockOverviewForSymbol(stockSymbol.toUpperCase());
    }

    @GetMapping("/{symbol}/history")
    public List<DailyStockResponse> getStockHistory(@PathVariable String symbol, @RequestParam(defaultValue = "30") int days){
        return stockService.getHistory(symbol.toUpperCase(), days);
    }

    @PostMapping("/favorites")
    public ResponseEntity<FavoriteStock> saveFavoriteStock(@RequestBody FavoriteStockRequest favoriteStock){
        FavoriteStock savedStock = stockService.saveFavorite(favoriteStock.getSymbol());
        return ResponseEntity.ok(savedStock);
    }
}
