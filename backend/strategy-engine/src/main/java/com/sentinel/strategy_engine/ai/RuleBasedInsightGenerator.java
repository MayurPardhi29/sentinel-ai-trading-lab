package com.sentinel.strategy_engine.ai;

import com.sentinel.strategy_engine.dto.BacktestResult;
import com.sentinel.strategy_engine.dto.SentinelConsensus;
import org.springframework.stereotype.Component;

@Component
public class RuleBasedInsightGenerator implements InsightGenerator {

    @Override
    public String generate(String symbol, BacktestResult backtest, SentinelConsensus consensus) {

        StringBuilder sb = new StringBuilder();

        sb.append(symbol).append(": ");

        if (backtest.getTrades() == 0) {
            sb.append("No trades were triggered by this strategy over the selected window, ")
              .append("so there isn't enough data yet to judge its performance. ");
        } else {
            sb.append("Over ").append(backtest.getTrades()).append(" trades, the strategy returned ")
              .append(backtest.getTotalReturnPct()).append("% with a ")
              .append(backtest.getWinRate()).append("% win rate and a profit factor of ")
              .append(backtest.getProfitFactor()).append(". ");

            if (backtest.getProfitFactor() < 1.0) {
                sb.append("A profit factor below 1.0 means this strategy lost money net over the period. ");
            } else if (backtest.getProfitFactor() < 1.5) {
                sb.append("A profit factor this close to 1.0 suggests a thin edge - it could turn ")
                  .append("negative with real-world slippage and fees. ");
            } else {
                sb.append("A profit factor above 1.5 is a reasonably healthy edge, though past results ")
                  .append("don't guarantee future ones. ");
            }

            double gap = backtest.getBenchmarkReturnPct() - backtest.getTotalReturnPct();
            if (gap > 10) {
                sb.append("It significantly underperformed simply buying and holding (")
                  .append(backtest.getBenchmarkReturnPct()).append("% vs ")
                  .append(backtest.getTotalReturnPct()).append("%), which usually means the strategy is ")
                  .append("exiting winning trades too early during a strong trend. ");
            } else if (gap < -10) {
                sb.append("It outperformed simply buying and holding by a wide margin, which is worth ")
                  .append("noting but also worth stress-testing against a longer or different history. ");
            }

            sb.append("Max drawdown was ").append(backtest.getMaxDrawdownPct())
              .append("%, and the Sharpe ratio was ").append(backtest.getSharpeRatio()).append(". ");
        }

        if (consensus != null) {
            sb.append("Right now, EMA is signaling ").append(consensus.getEma())
              .append(" and FVG is signaling ").append(consensus.getFvg())
              .append(" (").append(consensus.getAgreement()).append("% agreement), giving a ")
              .append(consensus.getRecommendation()).append(" recommendation. ")
              .append(consensus.getEntryHint());
        }

        return sb.toString();
    }
}
