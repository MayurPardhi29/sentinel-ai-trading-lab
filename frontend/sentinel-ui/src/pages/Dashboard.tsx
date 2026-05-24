import { useState, useEffect } from "react";
import { strategyApi } from "../services/strategyApi";
import "./Dashboard.css";

import PriceChart from "../components/PriceChart";
import { calculateEMA } from "../services/calculateEMA";
import { explain } from "../services/explain";

function Dashboard() {
    const [symbol, setSymbol] = useState("AAPL");
    const [strategy, setStrategy] = useState("ema");
    const [fast, setFast] = useState(9);
    const [slow, setSlow] = useState(21);

    const [result, setResult] = useState<any>();
    const [backtest, setBacktest] = useState<any>();

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [chart, setChart] = useState<any[]>([]);

    useEffect(() => {
        analyze();
    }, []);

    async function analyze() {
        try {
            setLoading(true);
            setError("");

            const strategyResult =
                await strategyApi.post(
                    `/api/strategy/playground/${strategy}/${symbol}`,
                    {
                        fast,
                        slow
                    }
                );

            const backtestResult =
                await strategyApi.get(
                    `/api/strategy/backtest/${strategy}/${symbol}`
                );

            const candleResult =
                await strategyApi.get(
                    `/api/strategy/candles/${symbol}`
                );
                
            setResult(
                strategyResult.data
            );

            setBacktest(
                backtestResult.data
            );

            const base =

                candleResult
                    .data
                    .values
                    .map(
                        (c: any) => ({

                            datetime:
                                c.datetime,

                            close:
                                Number(
                                    c.close
                                )

                        })
                    );

            const emaFast =

                calculateEMA(
                    base,
                    fast
                );

            const emaSlow =

                calculateEMA(
                    base,
                    slow
                );

            const merged =

                base.map(
                    (
                        item,
                        i
                    ) => ({

                        ...item,

                        emaFast:
                            emaFast[i]
                                .ema,

                        emaSlow:
                            emaSlow[i]
                                .ema

                    })
                );

            setChart(
                merged
            );
        } catch {
            setError(
                "Unable to analyze symbol"
            );
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="dashboard">

            <div className="card">

                <h1 className="title">
                    Sentinel Dashboard 📈
                </h1>

                <div className="input-group">

                    <input
                        value={symbol}
                        onChange={(e) =>
                            setSymbol(
                                e.target.value
                            )
                        }
                    />

                    <select
                        value={strategy}
                        onChange={(e) =>
                            setStrategy(
                                e.target.value
                            )
                        }
                    >
                        <option value="ema">
                            EMA
                        </option>

                        <option value="fvg">
                            FVG
                        </option>

                    </select>

                </div>

                {
                    strategy === "ema" && (
                <div className="input-group">

                    <input
                        type="number"
                        value={fast}
                        onChange={(e) =>
                            setFast(
                                Number(
                                    e.target.value
                                )
                            )
                        }
                    />

                    <input
                        type="number"
                        value={slow}
                        onChange={(e) =>
                            setSlow(
                                Number(
                                    e.target.value
                                )
                            )
                        }
                    />

                </div>
            )}
                <button
                    onClick={analyze}
                    disabled={loading}
                >
                    {
                        loading
                            ? "Analyzing..."
                            : "Analyze"
                    }
                </button>

                {error && (
                    <p>
                        {error}
                    </p>
                )}

            </div>

            {loading && (

                <div className="card">

                    Loading market data...

                </div>

            )}

            {chart.length > 0 && (

                <div className="card">

                    <h2>
                        Price
                    </h2>

                    <PriceChart
                        data={chart}
                    />

                </div>

            )}

            {result && (

                <div className="card result">

                    <h2>
                        Strategy Result
                    </h2>

                    <p className="stat">
                        Pattern: {result.pattern}
                    </p>

                    <p className="stat">
                        Signal{" "}
                        {
                            result.detected
                                ? "BUY"
                                : "WAIT"
                        }
                    </p>

                    <p className="stat">
                        Confidence{" "}
                        {result.confidence}
                    </p>

                    <p className = "stat">
                        Summary: {
                            explain(
                                result.detected,
                                result.confidence
                            )
                        }
                    </p>

                </div>

            )}

            {backtest && (

                <div className="card">

                    <h2>
                        Backtest
                    </h2>

                    <p className="stat">
                        Trades {backtest.trades}
                    </p>

                    <p className="stat">
                        Win Rate {backtest.winRate}%
                    </p>

                    <p className="stat">
                        Profit Factor {backtest.profitFactor}
                    </p>

                </div>

            )}

        </div>
    );
}

export default Dashboard;