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

    // Previously this was hardcoded to 30 in the request URL itself, which left barely
    // any usable candles once a strategy's EMA lookback was subtracted — not enough
    // data for a backtest result to mean anything statistically.
    @Value("${twelvedata.max-outputsize:500}")
    private int maxOutputSize = 500;

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
    public HistoricalCandleResponse fetchHistoricalData(String symbol, String interval, int outputSize) {

        int size = clamp(outputSize);

        String url = baseUrl
                + "/time_series?symbol="
                + symbol
                + "&interval="
                + interval
                + "&outputsize=" + size
                + "&apikey="
                + apiKey;

        return restTemplate.getForObject(
                url,
                HistoricalCandleResponse.class
        );
    }

    private int clamp(int requested) {
        if (requested <= 0) {
            return maxOutputSize;
        }
        return Math.min(requested, maxOutputSize); //To safeguard the search window
    }
}
