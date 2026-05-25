import { useState, useEffect } from "react";
import { strategyApi } from "../services/strategyApi";
import { explain } from "../services/explain";
import PriceChart from "../components/PriceChart";
import "./Dashboard.css";

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

    const [consensus, setConsensus] = useState<any>();

    const [timeframe, setTimeframe] = useState("1day");

    const [showPrice, setShowPrice] = useState(true);
    const [showEMA, setShowEMA] = useState(true);
    const [showVolume, setShowVolume] = useState(false);

    const popular = [
        "AAPL",
        "TSLA",
        "MSFT",
        "NVDA",
        "GOOGL"
    ]
    useEffect(() => {
        analyze();
    }, [
        strategy,
        timeframe
    ]);

    async function analyze() {
        try {
            setLoading(true);
            setError("");

            const strategyResult = await strategyApi.post(
                `/api/strategy/playground/${strategy}/${symbol}`,
                { fast, slow, interval: timeframe }
            );

            const backtestResult = await strategyApi.post(
                `/api/strategy/backtest/${strategy}/${symbol}`,
                { fast, slow, interval: timeframe }
            );

            const candleResult = await strategyApi.post(
                `/api/strategy/chart/${symbol}`,
                { fast, slow, interval: timeframe }
            );

            const consensusResult = await strategyApi.post(
                `/api/strategy/consensus/${symbol}`,
                { fast, slow, interval: timeframe }
            );

            setResult(strategyResult.data);
            setBacktest(backtestResult.data);

            setChart(

                candleResult
                    .data

                    .map(

                        (c: any) => ({

                            date:

                                new Date(
                                    c.datetime
                                )

                                    .toLocaleDateString(

                                        "en-IN",

                                        {

                                            day: "numeric",

                                            month: "short"

                                        }

                                    ),

                            open:
                                c.open,

                            high:
                                c.high,

                            low:
                                c.low,

                            close:
                                c.close,

                            volume:
                                c.volume,

                            emaFast:
                                c.emaFast,

                            emaSlow:
                                c.emaSlow,

                            signal:
                                c.signal,

                            event:
                                c.event,

                            signalType:
                                c.signalType
                        })

                    )

            );

            setConsensus(
                consensusResult.data
            );
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

                <div className="symbol-row">

                    {
                        popular.map(
                            (s) => (

                                <button
                                    key={s}
                                    className={
                                        symbol === s
                                            ? "chip active"
                                            : "chip"
                                    }
                                    onClick={() => {

                                        setSymbol(
                                            s
                                        );

                                        setTimeout(
                                            analyze,
                                            0
                                        );

                                    }}
                                >
                                    {s}
                                </button>

                            )
                        )
                    }

                </div>

                <div className="input-group">
                    <input
                        value={symbol}
                        placeholder="Ticker"

                        onChange={(e) =>

                            setSymbol(
                                e.target.value
                                    .toUpperCase()
                            )

                        }
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

                <select

                    value=
                    {timeframe}

                    onChange=

                    {

                        e =>

                            setTimeframe(
                                e.target.value
                            )

                    }

                >

                    <option value="1day">

                        1 Day

                    </option>

                    <option value="1week">

                        1 Week

                    </option>

                    <option value="1month">

                        1 Month

                    </option>

                </select>
                <button
                    onClick={analyze}
                    disabled={loading}
                >
                    {loading ? "Analyzing..." : "Analyze"}
                </button>

                {error && (
                    <div className="empty">

                        📭

                        <p>

                            No data available

                        </p>

                        <small>

                            Try another symbol

                        </small>

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

<h2>

Price

</h2>

<div className="toolbar">

<label>

<input
type="checkbox"

checked={
showPrice
}

onChange={()=>

setShowPrice(
!showPrice
)

}
/>

Price

</label>

<label>

<input
type="checkbox"

checked={
showEMA
}

onChange={()=>

setShowEMA(
!showEMA
)

}
/>

EMA

</label>

<label>

<input
type="checkbox"

checked={
showVolume
}

onChange={()=>

setShowVolume(
!showVolume
)

}
/>

Volume

</label>

</div>

<PriceChart

data={
chart
}

showPrice={
showPrice
}

showEMA={
showEMA
}

showVolume={
showVolume
}

/>

</div>

)}


            {result && (
                <div className="card">

                    <h2>Strategy Result</h2>

                    <div className="result">

                        <div className="stat">

                            Signal

                            <br />

                            <strong

                                className={
                                    result.detected
                                        ? "buy"
                                        : "wait"
                                }

                            >

                                {
                                    result.detected
                                        ? "🟢 BUY"
                                        : "🟡 WAIT"
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

                    <div className="result">
                        <div className="stat">
                            Trades
                            <br />
                            <strong>
                                {backtest.trades}
                            </strong>
                        </div>

                        <div className="stat">
                            Win Rate
                            <br />
                            <strong>
                                {backtest.winRate}%
                            </strong>
                        </div>

                        <div className="stat">
                            Wins
                            <br />
                            <strong>
                                {backtest.wins}
                            </strong>
                        </div>

                        <div className="stat">
                            Losses
                            <br />
                            <strong>
                                {backtest.losses}
                            </strong>
                        </div>

                        <div className="stat">
                            Profit Factor
                            <br />
                            <strong>
                                {backtest.profitFactor}
                            </strong>
                        </div>

                    </div>

                    {!!backtest.history?.length && (

                        <>

                            <h3
                                style={{
                                    marginTop: 24
                                }}
                            >
                                Recent Trades
                            </h3>

                            <div
                                style={{
                                    display: "grid",
                                    gap: 10
                                }}
                            >

                                {
                                    backtest.history
                                        .slice(0, 5)
                                        .map(
                                            (
                                                t: any,
                                                i: number
                                            ) => (

                                                <div
                                                    key={i}
                                                    className="stat"
                                                >

                                                    <strong>
                                                        {t.date}
                                                    </strong>

                                                    <br />

                                                    {t.signal}

                                                    {" • "}

                                                    {
                                                        t.result === "WIN"
                                                            ? "🟢 WIN"
                                                            : "🔴 LOSS"
                                                    }

                                                </div>

                                            )
                                        )
                                }

                            </div>

                        </>

                    )}

                </div>

            )}

            {

                consensus

                &&

                (

                    <div
                        className=
                        "card"
                    >

                        <h2>

                            Sentinel Consensus

                        </h2>

                        <div className="result">

                            <div className="stat">

                                EMA

                                <br />

                                <strong>

                                    {
                                        consensus.ema
                                    }

                                </strong>

                            </div>

                            <div className="stat">

                                FVG

                                <br />

                                <strong>

                                    {
                                        consensus.fvg
                                    }

                                </strong>

                            </div>

                            <div className="stat">

                                Agreement

                                <br />

                                <strong>

                                    {
                                        consensus.agreement
                                    }

                                    %

                                </strong>

                            </div>

                            <div className="stat">

                                Recommendation

                                <br />

                                <strong>

                                    {
                                        consensus.recommendation
                                    }

                                </strong>

                            </div>

                        </div>

                    </div>

                )

            }
            <footer
                className="footer"
            >

                Sentinel V1

                <br />

                Strategy Intelligence Dashboard

            </footer>
        </div>
    );
}

export default Dashboard;