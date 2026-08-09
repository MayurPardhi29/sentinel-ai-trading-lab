package com.sentinel.strategy_engine.client;

import com.sentinel.strategy_engine.dto.HistoricalCandleResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
public class MarketDataClient {

    private final RestTemplate restTemplate;

    public MarketDataClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }
    @Value(
            "${MARKET_DATA_URL:http://localhost:8080}"
    )
    private String baseUrl;

    public HistoricalCandleResponse
    getHistoricalData(
            String symbol,
            String interval
    ) {




        String url =
                baseUrl
                        +
                        "/api/stocks/history/"
                        +
                        symbol
                        +
                        "?interval="
                        +
                        interval;

        HistoricalCandleResponse response =

                restTemplate
                        .getForObject(
                                url,
                                HistoricalCandleResponse.class
                        );

        if(response == null ||
                response.getValues() == null ||
                response.getValues().isEmpty() ){
            throw new RuntimeException("no historical data found for "
                    +
                    symbol
            );
        }

        Collections.reverse(
                response.getValues()
        );

        return response;
    }
}
