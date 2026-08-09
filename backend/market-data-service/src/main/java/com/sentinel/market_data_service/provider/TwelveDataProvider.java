package com.sentinel.market_data_service.provider;

import com.sentinel.market_data_service.dto.HistoricalCandleResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TwelveDataProvider implements StockDataProvider {

    private final RestTemplate restTemplate;

    @Value("${twelvedata.api.key}")
    private String apiKey;

    @Value("${twelvedata.base.url}")
    private String baseUrl;

    public TwelveDataProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String fetchQuote(String symbol) {

        String url = baseUrl
                + "/price?symbol="
                + symbol
                + "&apikey="
                + apiKey;

        return restTemplate.getForObject(url, String.class);
    }

    @Override
    public HistoricalCandleResponse fetchHistoricalData(String symbol, String interval) {

        String url = baseUrl
                + "/time_series?symbol="
                + symbol
                + "&interval="
                + interval
                + "&outputsize=30"
                + "&apikey="
                + apiKey;

        return restTemplate.getForObject(
                url,
                HistoricalCandleResponse.class
        );
    }
}
