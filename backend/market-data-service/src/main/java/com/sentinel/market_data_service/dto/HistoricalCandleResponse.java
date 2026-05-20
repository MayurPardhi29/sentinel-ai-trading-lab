package com.sentinel.market_data_service.dto;
import java.util.List;

public class HistoricalCandleResponse {

    private List<CandleData> values;

    public List<CandleData> getValues() {
        return values;
    }

    public void setValues(List<CandleData> values) {
        this.values = values;
    }
}