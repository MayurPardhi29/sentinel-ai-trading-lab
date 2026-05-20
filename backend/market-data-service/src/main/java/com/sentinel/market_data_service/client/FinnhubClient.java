package com.sentinel.market_data_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FinnhubClient {

    private final RestTemplate restTemplate;

    @Value("${finnhub.api.key}")
    private String apiKey;

    @Value("${finnhub.base.url}")
    private String baseUrl;

    public FinnhubClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public String fetchStockQuote(String symbol){
        String url = baseUrl
                + "/quote?symbol="
                + symbol
                + "&token="
                + apiKey;

        return restTemplate.getForObject(url, String.class);
    }
}
