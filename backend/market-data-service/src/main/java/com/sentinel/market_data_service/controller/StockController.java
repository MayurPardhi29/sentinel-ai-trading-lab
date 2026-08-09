package com.sentinel.market_data_service.controller;

import com.sentinel.market_data_service.dto.HistoricalCandleResponse;
import org.springframework.web.bind.annotation.*;
import com.sentinel.market_data_service.service.StockService;

@RestController
@RequestMapping(
        "/api/stocks"
)
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/{symbol}")
    public String getStock(@PathVariable String symbol){
        return stockService.getStockQuote(symbol);
    }

    @GetMapping(
            "/history/{symbol}"
    )

    public HistoricalCandleResponse history(

            @PathVariable
            String symbol,

            @RequestParam(

                    defaultValue=
                            "1day"

            )

            String interval

    ){

        return stockService
                .getHistoricalData(

                        symbol,

                        interval

                );

    }
}
