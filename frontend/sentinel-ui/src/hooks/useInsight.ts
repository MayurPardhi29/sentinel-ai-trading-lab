import { useState } from "react";
import { strategyApi } from "../services/strategyApi";

interface InsightConfig {
    symbol: string;
    strategy: string;
    fast: number;
    slow: number;
    timeframe: string;
}

export function useInsight() {
    const [insight, setInsight] = useState<any>();
    const [insightLoading, setInsightLoading] = useState(false);
    const [insightError, setInsightError] = useState("");

    async function fetchInsight(config: InsightConfig) {
        const { symbol, strategy, fast, slow, timeframe } = config;

        try {
            setInsightLoading(true);
            setInsightError("");

            const response = await strategyApi.post(
                `/api/strategy/insight/${strategy}/${symbol}`,
                { fast, slow, interval: timeframe }
            );

            setInsight(response.data);
        } catch {
            setInsightError("Unable to generate insight right now.");
        } finally {
            setInsightLoading(false);
        }
    }

    function resetInsight() {
        setInsight(undefined);
        setInsightError("");
    }

    return { insight, insightLoading, insightError, fetchInsight, resetInsight };
}
