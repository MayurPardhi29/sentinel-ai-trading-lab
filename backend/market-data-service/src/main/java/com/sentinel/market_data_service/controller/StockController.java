package com.sentinel.market_data_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.sentinel.market_data_service.service.StockService;

@RestController
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/api/stocks/{symbol}")
    public String getStock(@PathVariable String symbol){
        return stockService.getStockQuote(symbol);
    }
}
