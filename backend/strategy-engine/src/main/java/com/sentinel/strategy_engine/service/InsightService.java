
package com.sentinel.strategy_engine.service;

import com.sentinel.strategy_engine.ai.GroqInsightGenerator;
import com.sentinel.strategy_engine.ai.RuleBasedInsightGenerator;
import com.sentinel.strategy_engine.dto.BacktestResult;
import com.sentinel.strategy_engine.dto.InsightResponse;
import com.sentinel.strategy_engine.dto.SentinelConsensus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InsightService {

    private static final Logger log = LoggerFactory.getLogger(InsightService.class);

    private final GroqInsightGenerator groqInsightGenerator;
    private final RuleBasedInsightGenerator ruleBasedInsightGenerator;

    public InsightService(
            GroqInsightGenerator groqInsightGenerator,
            RuleBasedInsightGenerator ruleBasedInsightGenerator) {

        this.groqInsightGenerator = groqInsightGenerator;
        this.ruleBasedInsightGenerator = ruleBasedInsightGenerator;
    }

    public InsightResponse generate(
            String symbol,
            BacktestResult backtest,
            SentinelConsensus consensus) {

        if (groqInsightGenerator.isConfigured()) {
            try {

                String summary =
                        groqInsightGenerator.generate(symbol, backtest, consensus);

                return new InsightResponse(summary, "AI");

            } catch (Exception e) {

                log.warn(
                        "Groq insight generation failed, falling back to rule-based summary: {}",
                        e.getMessage()
                );
            }
        }

        String summary =
                ruleBasedInsightGenerator.generate(symbol, backtest, consensus);

        return new InsightResponse(summary, "RULE_BASED");
    }
}