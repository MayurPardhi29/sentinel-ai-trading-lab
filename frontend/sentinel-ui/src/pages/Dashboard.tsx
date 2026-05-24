import { useState, useEffect } from "react";
import { strategyApi } from "../services/strategyApi";
import { explain } from "../services/explain";
import PriceChart from "../components/PriceChart";
import "./Dashboard.css";
import { calculateEMA }
    from "../services/calculateEMA";

function Dashboard() {
    const [symbol, setSymbol] = useState("AAPL");
    const [strategy, setStrategy] = useState("ema");
    const [fast, setFast] = useState(9);
    const [slow, setSlow] = useState(21);

    const [result, setResult] = useState<any>();
    const [backtest, setBacktest] = useState<any>();
    const [chart, setChart] = useState<any[]>([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        analyze();
    }, []);

    async function analyze() {
        try {
            setLoading(true);
            setError("");

            const strategyResult = await strategyApi.post(
                `/api/strategy/playground/${strategy}/${symbol}`,
                { fast, slow }
            );

            const backtestResult = await strategyApi.get(
                `/api/strategy/backtest/${strategy}/${symbol}`
            );

            const candleResult = await strategyApi.get(
                `/api/strategy/candles/${symbol}`
            );

            setResult(strategyResult.data);
            setBacktest(backtestResult.data);

            const candles =

                candleResult
                    .data
                    .values

                    .map(

                        (c: any) => ({

                            date:

                                new Date(
                                    c.datetime
                                ).toLocaleDateString(

                                    "en-IN",

                                    {
                                        day: "numeric",
                                        month: "short"
                                    }

                                ),

                            open:
                                Number(c.open),

                            high:
                                Number(c.high),

                            low:
                                Number(c.low),

                            close:
                                Number(c.close),

                            volume:
                                Number(c.volume)

                        })

                    );

            if (
                strategy === "ema"
            ) {

                const fastEMA =

                    calculateEMA(
                        candles,
                        fast
                    );

                const slowEMA =

                    calculateEMA(
                        candles,
                        slow
                    );

                setChart(

                    candles.map(

                        (

                            c,
                            i

                        ) => ({

                            ...c,

                            emaFast:
                                fastEMA[i]?.ema,

                            emaSlow:
                                slowEMA[i]?.ema

                        })

                    )

                );

            }

            else {

                setChart(
                    candles
                );

            }
            ;
        } catch {
            setError("Unable to analyze symbol");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="dashboard">

            <header className="header">

                <div>
                    <h1 className="brand">
                        Sentinel
                    </h1>

                    <p className="subtitle">
                        Real-time Strategy Playground
                    </p>
                </div>

                <div className="live">
                    ● LIVE
                </div>

            </header>

            <div className="card controls">

                <div className="input-group">
                    <input
                        value={symbol}
                        onChange={(e) => setSymbol(e.target.value)}
                    />

                    <select
                        value={strategy}
                        onChange={(e) => setStrategy(e.target.value)}
                    >
                        <option value="ema">EMA</option>
                        <option value="fvg">FVG</option>
                    </select>
                </div>

                {strategy === "ema" && (
                    <div className="input-group">
                        <input
                            type="number"
                            value={fast}
                            onChange={(e) => setFast(Number(e.target.value))}
                        />

                        <input
                            type="number"
                            value={slow}
                            onChange={(e) => setSlow(Number(e.target.value))}
                        />
                    </div>
                )}

                <button
                    onClick={analyze}
                    disabled={loading}
                >
                    {loading ? "Analyzing..." : "Analyze"}
                </button>

                {error && (
                    <p>{error}</p>
                )}

            </div>

            {loading && (
                <div className="card">
                    Loading market data...
                </div>
            )}

            {chart.length > 0 && (
                <div className="card">
                    <h2>Price</h2>
                    <PriceChart data={chart} />
                </div>
            )}

            {result && (
                <div className="card">

                    <h2>Strategy Result</h2>

                    <div className="result">

                        <div className="stat">
                            Signal
                            <br />

                            <strong>
                                {
                                    result.detected
                                        ? "BUY"
                                        : "WAIT"
                                }
                            </strong>
                        </div>

                        <div className="stat">
                            Confidence
                            <br />

                            <strong>
                                {
                                    Math.round(
                                        result.confidence
                                    )
                                }
                                %
                            </strong>
                        </div>

                        <div className="stat">
                            Pattern
                            <br />

                            <strong>
                                {
                                    result.pattern
                                }
                            </strong>
                        </div>

                        <div className="stat">
                            Summary
                            <br />

                            {
                                explain(
                                    result.detected,
                                    result.confidence
                                )
                            }
                        </div>

                    </div>

                </div>
            )}

            {backtest && (
                <div className="card">

                    <h2>Backtest</h2>

                    <p className="stat">
                        Trades: {backtest.trades}
                    </p>

                    <p className="stat">
                        Win Rate: {backtest.winRate}%
                    </p>

                    <p className="stat">
                        Profit Factor: {backtest.profitFactor}
                    </p>

                </div>
            )}

        </div>
    );
}

export default Dashboard;