import { useState } from "react";
import { strategyApi } from "../services/strategyApi";
import { formatChartDate } from "../utils/formatters";

interface AnalysisConfig {
    symbol: string;
    strategy: string;
    fast: number;
    slow: number;
    timeframe: string;
}

export function useMarketAnalysis() {
    const [result, setResult] = useState<any>();
    const [backtest, setBacktest] = useState<any>();
    const [chart, setChart] = useState<any[]>([]);
    const [consensus, setConsensus] = useState<any>();

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    async function analyze(config: AnalysisConfig) {
        const { symbol, strategy, fast, slow, timeframe } = config;

        try {
            setLoading(true);
            setError("");

            const payload = { fast, slow, interval: timeframe };

            const strategyResult = await strategyApi.post(
                `/api/strategy/playground/${strategy}/${symbol}`,
                payload
            );

            const backtestResult = await strategyApi.post(
                `/api/strategy/backtest/${strategy}/${symbol}`,
                payload
            );

            const candleResult = await strategyApi.post(
                `/api/strategy/chart/${symbol}`,
                payload
            );

            const consensusResult = await strategyApi.post(
                `/api/strategy/consensus/${symbol}`,
                payload
            );

            setResult(strategyResult.data);
            setBacktest(backtestResult.data);

            // rawDate is kept alongside the formatted display label so the date-range
            // filter can compare actual timestamps, not the already-formatted string.
            setChart(
                candleResult.data.map((c: any) => ({
                    rawDate: c.datetime,
                    date: formatChartDate(c.datetime, timeframe),
                    open: c.open,
                    high: c.high,
                    low: c.low,
                    close: c.close,
                    volume: c.volume,
                    emaFast: c.emaFast,
                    emaSlow: c.emaSlow,
                    signal: c.signal,
                    event: c.event,
                    signalType: c.signalType
                }))
            );

            setConsensus(consensusResult.data);

            return true;
        } catch {
            setError("Unable to analyze symbol");
            return false;
        } finally {
            setLoading(false);
        }
    }

    return { result, backtest, chart, consensus, loading, error, analyze };
}
