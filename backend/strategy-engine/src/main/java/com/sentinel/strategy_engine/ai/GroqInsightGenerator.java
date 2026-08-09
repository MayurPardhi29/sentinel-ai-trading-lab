package com.sentinel.strategy_engine.ai;

import com.sentinel.strategy_engine.dto.BacktestResult;
import com.sentinel.strategy_engine.dto.SentinelConsensus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class GroqInsightGenerator implements InsightGenerator {

    private final RestTemplate restTemplate;

    @Value("${groq.api.key:}")
    private String apiKey;

    @Value("${groq.model:llama-3.3-70b-versatile}")
    private String model;

    @Value("${groq.base-url:https://api.groq.com/openai/v1}")
    private String baseUrl;

    public GroqInsightGenerator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    @Override
    public String generate(String symbol,
                           BacktestResult backtest,
                           SentinelConsensus consensus) {

        if (!isConfigured()) {
            throw new IllegalStateException("Groq API key is not configured");
        }

        String prompt = buildPrompt(symbol, backtest, consensus);

        String url = baseUrl + "/chat/completions";

        Map<String, Object> requestBody = Map.of(

                "model", model,

                "messages", List.of(

                        Map.of(
                                "role", "system",
                                "content",
                                """
                                You are a professional quantitative trading analyst.

                                Only interpret the supplied numbers.

                                Never invent:
                                - market news
                                - future prices
                                - support/resistance
                                - targets
                                - company fundamentals

                                Explain:
                                - strategy quality
                                - risk
                                - current signal
                                - strengths
                                - weaknesses

                                Keep the response to 3-4 concise sentences.
                                Return plain text only.
                                """
                        ),

                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                ),

                "temperature", 0.2
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(requestBody, headers);

        @SuppressWarnings("unchecked")
        Map<String, Object> response =
                restTemplate.postForObject(
                        url,
                        entity,
                        Map.class
                );

        return extractText(response);
    }

    private String buildPrompt(String symbol,
                               BacktestResult backtest,
                               SentinelConsensus consensus) {

        StringBuilder sb = new StringBuilder();

        sb.append("Stock: ").append(symbol).append("\n\n");

        if (backtest != null) {

            sb.append("Backtest Results\n");
            sb.append("----------------\n");
            sb.append("Trades: ").append(backtest.getTrades()).append("\n");
            sb.append("Win Rate: ").append(backtest.getWinRate()).append("%\n");
            sb.append("Profit Factor: ").append(backtest.getProfitFactor()).append("\n");
            sb.append("Strategy Return: ").append(backtest.getTotalReturnPct()).append("%\n");
            sb.append("Buy & Hold Return: ").append(backtest.getBenchmarkReturnPct()).append("%\n");
            sb.append("Maximum Drawdown: ").append(backtest.getMaxDrawdownPct()).append("%\n");
            sb.append("Sharpe Ratio: ").append(backtest.getSharpeRatio()).append("\n");
            sb.append("Expectancy: ").append(backtest.getExpectancy()).append("\n\n");
        }

        if (consensus != null) {

            sb.append("Current Strategy Signal\n");
            sb.append("-----------------------\n");
            sb.append("EMA Signal: ").append(consensus.getEma()).append("\n");
            sb.append("FVG Signal: ").append(consensus.getFvg()).append("\n");
            sb.append("Agreement: ").append(consensus.getAgreement()).append("%\n");
            sb.append("Recommendation: ").append(consensus.getRecommendation()).append("\n");
            sb.append("Interval: ").append(consensus.getInterval()).append("\n\n");
        }

        sb.append("""
                Summarize what these metrics indicate.

                Mention:
                - overall strategy performance
                - comparison with buy-and-hold
                - risk based on drawdown
                - quality of the current trading signal

                Be objective and concise.
                """);

        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {

        if (response == null) {
            throw new IllegalStateException("Groq returned an empty response");
        }

        Object choicesObj = response.get("choices");

        if (!(choicesObj instanceof List<?> choices) || choices.isEmpty()) {
            throw new IllegalStateException("Groq response had no choices");
        }

        Map<String, Object> firstChoice =
                (Map<String, Object>) choices.get(0);

        Map<String, Object> message =
                (Map<String, Object>) firstChoice.get("message");

        if (message == null) {
            throw new IllegalStateException("Groq response missing message");
        }

        Object content = message.get("content");

        if (content == null || content.toString().isBlank()) {
            throw new IllegalStateException("Groq returned empty content");
        }

        return content.toString().trim();
    }
}