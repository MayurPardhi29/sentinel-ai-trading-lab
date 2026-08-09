import { useEffect, useState } from "react";
import { explain } from "../services/explain";
import PriceChart from "../components/PriceChart";
import {
    ResponsiveContainer,
    LineChart,
    Line,
    XAxis,
    YAxis,
    Tooltip,
    CartesianGrid,
    ReferenceLine
} from "recharts";
import { useMarketAnalysis } from "../hooks/useMarketAnalysis";
import { useInsight } from "../hooks/useInsight";
import { useDateRangeFilter } from "../hooks/useDateRangeFilter";
import { formatChartDate } from "../utils/formatters";
import "./Dashboard.css";

const POPULAR_SYMBOLS = ["AAPL", "TSLA", "MSFT", "NVDA", "GOOGL"];

function Dashboard() {
    const [symbol, setSymbol] = useState("AAPL");
    const [strategy, setStrategy] = useState("ema");
    const [fast, setFast] = useState(9);
    const [slow, setSlow] = useState(21);
    const [timeframe, setTimeframe] = useState("1day");

    const [showPrice, setShowPrice] = useState(true);
    const [showEMA, setShowEMA] = useState(true);

    const { result, backtest, chart, consensus, loading, error, analyze } = useMarketAnalysis();
    const { insight, insightLoading, insightError, fetchInsight, resetInsight } = useInsight();
    const { from, to, setFrom, setTo, filteredChart, minDate, maxDate, isWithinRange } =
        useDateRangeFilter(chart);

    const config = { symbol, strategy, fast, slow, timeframe };

    useEffect(() => {
        runAnalysis();
    }, [strategy, timeframe]);

    async function runAnalysis() {
        await analyze(config);
        resetInsight();
    }

    function selectPopularSymbol(s: string) {
        setSymbol(s);
        setTimeout(() => analyze({ ...config, symbol: s }), 0);
    }

    // Turns backtest trade history into chart markers, using the same date
    // formatting as the chart's x-axis so entry/exit points line up with the
    // correct candle. Also filtered to the currently selected date range so a
    // marker outside the visible window doesn't try to render off-chart.
    function buildTradeMarkers() {
        if (!backtest?.history?.length) {
            return [];
        }

        return backtest.history
            .filter((t: any) => isWithinRange(t.entryDate) || isWithinRange(t.exitDate))
            .flatMap((t: any) => {
                const exitType: "WIN" | "LOSS" = t.result === "WIN" ? "WIN" : "LOSS";

                return [
                    {
                        date: formatChartDate(t.entryDate, timeframe),
                        price: t.entryPrice,
                        type: "ENTRY" as const
                    },
                    {
                        date: formatChartDate(t.exitDate, timeframe),
                        price: t.exitPrice,
                        type: exitType
                    }
                ];
            });
    }

    return (
        <div className="dashboard">

            <header className="header">
                <div>
                    <h1 className="brand">Sentinel</h1>
                    <p className="subtitle">Real-time Strategy Playground</p>
                </div>

                <div className="live">● LIVE</div>
            </header>

            <div className="card controls">

                <div className="symbol-row">
                    {POPULAR_SYMBOLS.map((s) => (
                        <button
                            key={s}
                            className={symbol === s ? "chip active" : "chip"}
                            onClick={() => selectPopularSymbol(s)}
                        >
                            {s}
                        </button>
                    ))}
                </div>

                <div className="input-group">
                    <input
                        value={symbol}
                        placeholder="Ticker"
                        onChange={(e) => setSymbol(e.target.value.toUpperCase())}
                    />

                    <select value={strategy} onChange={(e) => setStrategy(e.target.value)}>
                        <option value="ema">EMA</option>
                        <option value="fvg">FVG</option>
                    </select>
                </div>

                {strategy === "ema" && (
                    <div className="input-group">
                        <label className="field-label">
                            Fast EMA
                            <input
                                type="number"
                                value={fast}
                                onChange={(e) => setFast(Number(e.target.value))}
                            />
                        </label>

                        <label className="field-label">
                            Slow EMA
                            <input
                                type="number"
                                value={slow}
                                onChange={(e) => setSlow(Number(e.target.value))}
                            />
                        </label>
                    </div>
                )}

                <div className="input-group">
                    <select value={timeframe} onChange={(e) => setTimeframe(e.target.value)}>
                        <option value="1min">1 Minute</option>
                        <option value="5min">5 Minutes</option>
                        <option value="15min">15 Minutes</option>
                        <option value="30min">30 Minutes</option>
                        <option value="1h">1 Hour</option>
                        <option value="4h">4 Hours</option>
                        <option value="1day">1 Day</option>
                        <option value="1week">1 Week</option>
                        <option value="1month">1 Month</option>
                    </select>

                    <button onClick={runAnalysis} disabled={loading}>
                        {loading ? "Analyzing..." : "Analyze"}
                    </button>
                </div>

                {error && (
                    <div className="empty">
                        📭
                        <p>No data available</p>
                        <small>Try another symbol</small>
                    </div>
                )}

            </div>

            {loading && (
                <div className="card">
                    <div className="skeleton" />
                    <div className="skeleton" />
                    <div className="skeleton" />
                </div>
            )}

            {chart.length > 0 && (
                <div className="card">
                    <h2>Price</h2>

                    <div className="toolbar">
                        <label>
                            <input
                                type="checkbox"
                                checked={showPrice}
                                onChange={() => setShowPrice(!showPrice)}
                            />
                            Price
                        </label>

                        <label>
                            <input
                                type="checkbox"
                                checked={showEMA}
                                onChange={() => setShowEMA(!showEMA)}
                            />
                            EMA
                        </label>
                    </div>

                    {!!minDate && (
                        <div className="date-range-row">
                            <label className="field-label">
                                From
                                <input
                                    type="datetime-local"
                                    value={from}
                                    min={minDate}
                                    max={to || maxDate}
                                    onChange={(e) => setFrom(e.target.value)}
                                />
                            </label>

                            <label className="field-label">
                                To
                                <input
                                    type="datetime-local"
                                    value={to}
                                    min={from || minDate}
                                    max={maxDate}
                                    onChange={(e) => setTo(e.target.value)}
                                />
                            </label>

                            <small className="range-hint">
                                Narrows the view within the {chart.length} candles already loaded for this
                                timeframe - it doesn't fetch further back in history.
                            </small>
                        </div>
                    )}

                    <PriceChart
                        data={filteredChart}
                        trades={buildTradeMarkers()}
                        showPrice={showPrice}
                        showEMA={showEMA}
                    />

                    {!!backtest?.history?.length && (
                        <div className="marker-legend">
                            <span><span style={{ color: "#3b82f6" }}>●</span> Entry</span>
                            <span><span style={{ color: "#26a69a" }}>●</span> Win exit</span>
                            <span><span style={{ color: "#ef5350" }}>●</span> Loss exit</span>
                        </div>
                    )}
                </div>
            )}

            {result && (
                <div className="card">
                    <h2>Strategy Result</h2>

                    <div className="result">
                        <div className="stat">
                            Signal
                            <br />
                            <strong className={result.detected ? "buy" : "wait"}>
                                {result.detected ? "🟢 BUY" : "🟡 WAIT"}
                            </strong>
                        </div>

                        <div className="stat">
                            Confidence
                            <br />
                            <strong>{Math.round(result.confidence)}%</strong>
                        </div>

                        <div className="stat">
                            Pattern
                            <br />
                            <strong>{result.pattern}</strong>
                        </div>

                        <div className="stat">
                            Summary
                            <br />
                            {explain(result.detected, result.confidence)}
                        </div>
                    </div>
                </div>
            )}

            {backtest && (
                <div className="card">
                    <h2>Backtest</h2>

                    <div className="result">
                        <div className="stat">
                            Trades
                            <br />
                            <strong>{backtest.trades}</strong>
                        </div>

                        <div className="stat">
                            Win Rate
                            <br />
                            <strong>{backtest.winRate}%</strong>
                        </div>

                        <div className="stat">
                            Wins
                            <br />
                            <strong>{backtest.wins}</strong>
                        </div>

                        <div className="stat">
                            Losses
                            <br />
                            <strong>{backtest.losses}</strong>
                        </div>

                        <div className="stat">
                            Profit Factor
                            <br />
                            <strong>{backtest.profitFactor}</strong>
                        </div>

                        <div className="stat">
                            Total Return
                            <br />
                            <strong style={{ color: backtest.totalReturnPct >= 0 ? "#26a69a" : "#ef5350" }}>
                                {backtest.totalReturnPct}%
                            </strong>
                        </div>

                        <div className="stat">
                            vs Buy &amp; Hold
                            <br />
                            <strong>{backtest.benchmarkReturnPct}%</strong>
                        </div>

                        <div className="stat">
                            Max Drawdown
                            <br />
                            <strong style={{ color: "#ef5350" }}>{backtest.maxDrawdownPct}%</strong>
                        </div>

                        <div className="stat">
                            Sharpe Ratio
                            <br />
                            <strong>{backtest.sharpeRatio}</strong>
                        </div>

                        <div className="stat">
                            Expectancy / Trade
                            <br />
                            <strong style={{ color: backtest.expectancy >= 0 ? "#26a69a" : "#ef5350" }}>
                                {backtest.expectancy}
                            </strong>
                        </div>
                    </div>

                    {!!backtest.equityCurve?.length && (
                        <>
                            <h3 style={{ marginTop: 24 }}>Equity Curve</h3>
                            <ResponsiveContainer width="100%" height={240}>
                                <LineChart data={backtest.equityCurve}>
                                    <CartesianGrid strokeDasharray="3 3" opacity={0.2} />
                                    <XAxis dataKey="date" tick={{ fontSize: 11 }} />
                                    <YAxis domain={["auto", "auto"]} tick={{ fontSize: 11 }} />
                                    <Tooltip />
                                    <ReferenceLine
                                        y={backtest.equityCurve[0]?.equity}
                                        stroke="#888"
                                        strokeDasharray="4 4"
                                        label={{ value: "Starting Capital", fontSize: 10, position: "insideTopLeft" }}
                                    />
                                    <Line
                                        type="monotone"
                                        dataKey="equity"
                                        stroke="#26a69a"
                                        dot={false}
                                        strokeWidth={2}
                                    />
                                </LineChart>
                            </ResponsiveContainer>
                        </>
                    )}

                    {!!backtest.history?.length && (
                        <>
                            <h3 style={{ marginTop: 24 }}>Recent Trades</h3>

                            <div style={{ display: "grid", gap: 10 }}>
                                {backtest.history.slice(0, 5).map((t: any, i: number) => (
                                    <div key={i} className="stat">
                                        <strong>{t.entryDate} → {t.exitDate}</strong>
                                        <br />
                                        {t.entryPrice} → {t.exitPrice}{" "}
                                        ({t.pnlPercent >= 0 ? "+" : ""}{t.pnlPercent}%)
                                        <br />
                                        <span style={{ opacity: 0.7, fontSize: 12 }}>{t.exitReason}</span>
                                        {" • "}
                                        {t.result === "WIN" ? "🟢 WIN" : "🔴 LOSS"}
                                    </div>
                                ))}
                            </div>
                        </>
                    )}
                </div>
            )}

            {consensus && (
                <div className="card">
                    <h2>Sentinel Consensus</h2>

                    {!!consensus.entryHint && (
                        <div
                            style={{
                                padding: "8px 12px",
                                marginBottom: 12,
                                borderRadius: 6,
                                fontSize: 14,
                                fontWeight: 600,
                                background: consensus.recommendation === "STRONG BUY" ? "#1e4620" : "#3a3a3a",
                                color: consensus.recommendation === "STRONG BUY" ? "#4ade80" : "#ccc",
                                border: consensus.recommendation === "STRONG BUY" ? "1px solid #2ecc71" : "1px solid #555"
                            }}
                        >
                            {consensus.entryHint}
                        </div>
                    )}

                    <div className="result">
                        <div className="stat">
                            EMA
                            <br />
                            <strong>{consensus.ema}</strong>
                        </div>

                        <div className="stat">
                            FVG
                            <br />
                            <strong>{consensus.fvg}</strong>
                        </div>

                        <div className="stat">
                            Agreement
                            <br />
                            <strong>{consensus.agreement}%</strong>
                        </div>

                        <div className="stat">
                            Recommendation
                            <br />
                            <strong>{consensus.recommendation}</strong>
                        </div>
                    </div>
                </div>
            )}

            {backtest && (
                <div className="card">
                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                        <h2 style={{ margin: 0 }}>AI Insight</h2>

                        <button
                            onClick={() => fetchInsight(config)}
                            disabled={insightLoading}
                            style={{
                                padding: "6px 14px",
                                borderRadius: 6,
                                border: "1px solid #555",
                                background: "#2a2a2a",
                                color: "#eee",
                                cursor: insightLoading ? "default" : "pointer",
                                fontSize: 13
                            }}
                        >
                            {insightLoading ? "Generating..." : insight ? "Regenerate" : "Generate Insight"}
                        </button>
                    </div>

                    {!!insightError && (
                        <p style={{ color: "#ef4444", fontSize: 13, marginTop: 8 }}>{insightError}</p>
                    )}

                    {!insight && !insightLoading && !insightError && (
                        <p style={{ opacity: 0.6, fontSize: 13, marginTop: 8 }}>
                            Summarizes this strategy's backtest and current signal in plain language.
                        </p>
                    )}

                    {!!insight && (
                        <>
                            <p style={{ marginTop: 12, lineHeight: 1.5 }}>{insight.summary}</p>

                            <span
                                style={{
                                    display: "inline-block",
                                    marginTop: 8,
                                    fontSize: 11,
                                    padding: "2px 8px",
                                    borderRadius: 12,
                                    background: insight.source === "AI" ? "#1e3a5f" : "#3a3a3a",
                                    color: insight.source === "AI" ? "#7dd3fc" : "#aaa"
                                }}
                            >
                                {insight.source === "AI" ? "Generated by Groq" : "Rule-based summary"}
                            </span>
                        </>
                    )}
                </div>
            )}

            <footer className="footer">
                Sentinel V1
                <br />
                Strategy Intelligence Dashboard
            </footer>
        </div>
    );
}

export default Dashboard;
