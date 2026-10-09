import {
    ResponsiveContainer,
    ComposedChart,
    Bar,
    Line,
    XAxis,
    YAxis,
    Tooltip,
    CartesianGrid,
    ReferenceDot,
    ReferenceLine
} from "recharts";
import { formatVolume } from "../utils/formatters";

interface Candle {
    date: string;
    open: number;
    high: number;
    low: number;
    close: number;
    volume: number;
    emaFast?: number;
    emaSlow?: number;
    signal?: boolean;
}

interface Trade {
    date: string;
    price: number;
    type: "ENTRY" | "WIN" | "LOSS";
}

interface Props {
    data: Candle[];
    showPrice: boolean;
    showEMA: boolean;
    trades?: Trade[];
}

const UP_COLOR = "#26a69a";
const DOWN_COLOR = "#ef5350";
const GRID_COLOR = "#1b2130";
const AXIS_COLOR = "#5b6478";
const BG_COLOR = "#0b0e14";

// Recharts has no built-in candlestick chart, so this renders each candle as a
// "range bar" (dataKey resolves to [low, high], which Recharts maps to the y/height
// of the bar's bounding box) with a custom shape that draws the high-low wick and
// then interpolates the open/close body rectangle within that same pixel range.
function Candlestick(props: any) {
    const { x, y, width, height, payload } = props;

    if (payload.open == null || payload.close == null) {
        return null;
    }

    const isUp = payload.close >= payload.open;
    const color = isUp ? UP_COLOR : DOWN_COLOR;

    const priceRange = payload.high - payload.low || 0.0001;
    const bodyTopPrice = Math.max(payload.open, payload.close);
    const bodyBottomPrice = Math.min(payload.open, payload.close);

    const bodyTop = y + height * (payload.high - bodyTopPrice) / priceRange;
    const bodyBottom = y + height * (payload.high - bodyBottomPrice) / priceRange;
    const bodyHeight = Math.max(bodyBottom - bodyTop, 1);

    const bodyWidth = Math.max(width * 0.62, 2);
    const bodyX = x + (width - bodyWidth) / 2;
    const wickX = x + width / 2;

    return (
        <g>
            <line
                x1={wickX}
                x2={wickX}
                y1={y}
                y2={y + height}
                stroke={color}
                strokeWidth={1}
            />
            <rect
                x={bodyX}
                y={bodyTop}
                width={bodyWidth}
                height={bodyHeight}
                fill={color}
                stroke={color}
            />
        </g>
    );
}

function VolumeBar(props: any) {
    const { x, y, width, height, payload } = props;

    if (payload.open == null || payload.close == null) {
        return <rect x={x} y={y} width={width} height={height} fill={AXIS_COLOR} opacity={0.25} />;
    }

    const isUp = payload.close >= payload.open;
    const color = isUp ? UP_COLOR : DOWN_COLOR;

    return <rect x={x} y={y} width={width} height={height} fill={color} opacity={0.35} />;
}

function BrokerTooltip({ active, payload, label }: any) {
    if (!active || !payload || !payload.length) {
        return null;
    }

    const candle: Candle = payload[0].payload;
    const isUp = candle.close >= candle.open;

    return (
        <div
            style={{
                background: "#11151f",
                border: "1px solid #232838",
                borderRadius: 8,
                padding: "10px 12px",
                fontSize: 12,
                fontFamily: "'SF Mono', 'Roboto Mono', monospace",
                color: "#e6e9f0",
                minWidth: 150
            }}
        >
            <div style={{ color: AXIS_COLOR, marginBottom: 6 }}>{label}</div>

            <div style={{ display: "grid", gridTemplateColumns: "auto auto", gap: "2px 12px" }}>
                <span style={{ color: AXIS_COLOR }}>O</span>
                <span>{candle.open?.toFixed(2)}</span>

                <span style={{ color: AXIS_COLOR }}>H</span>
                <span>{candle.high?.toFixed(2)}</span>

                <span style={{ color: AXIS_COLOR }}>L</span>
                <span>{candle.low?.toFixed(2)}</span>

                <span style={{ color: AXIS_COLOR }}>C</span>
                <span style={{ color: isUp ? UP_COLOR : DOWN_COLOR, fontWeight: 600 }}>
                    {candle.close?.toFixed(2)}
                </span>

                {candle.volume != null && (
                    <>
                        <span style={{ color: AXIS_COLOR }}>Vol</span>
                        <span>{formatVolume(candle.volume)}</span>
                    </>
                )}
            </div>
        </div>
    );
}

export default function PriceChart({
    data,
    showPrice,
    showEMA,
    trades = []
}: Props) {

    const lastClose = data.length ? data[data.length - 1].close : undefined;

    return (
        <div className="chart" style={{ background: BG_COLOR, borderRadius: 12, padding: "12px 8px" }}>
            <ResponsiveContainer>
                <ComposedChart data={data} margin={{ top: 8, right: 12, bottom: 4, left: 4 }}>

                    <CartesianGrid stroke={GRID_COLOR} strokeDasharray="2 4" vertical={false} />

                    <XAxis
                        dataKey="date"
                        tick={{ fill: AXIS_COLOR, fontSize: 11 }}
                        axisLine={{ stroke: GRID_COLOR }}
                        tickLine={false}
                        interval="equidistantPreserveStart"
                        minTickGap={24}
                    />

                    <YAxis
                        yAxisId="price"
                        domain={["auto", "auto"]}
                        tick={{ fill: AXIS_COLOR, fontSize: 11 }}
                        axisLine={false}
                        tickLine={false}
                        orientation="right"
                        tickFormatter={(v) => Number(v).toFixed(2)}
                        width={56}
                    />

                    <YAxis
                        yAxisId="volume"
                        orientation="left"
                        hide={true}
                        domain={[0, (max: number) => max * 4]}
                        tick={{ fill: AXIS_COLOR, fontSize: 10 }}
                        axisLine={false}
                        tickLine={false}
                    />

                    <Tooltip
                        content={<BrokerTooltip />}
                        cursor={{ stroke: "#334155", strokeWidth: 1, strokeDasharray: "3 3" }}
                    />

                    <Bar yAxisId="volume" dataKey="volume" shape={<VolumeBar />} isAnimationActive={false} />

                    {showPrice && (
                        <Bar
                            yAxisId="price"
                            dataKey={(d: Candle) => [d.low, d.high]}
                            shape={<Candlestick />}
                            isAnimationActive={false}
                        />
                    )}

                    {showEMA && data[0]?.emaFast != null && (
                        <Line
                            yAxisId="price"
                            dataKey="emaFast"
                            stroke="#f0b90b"
                            dot={false}
                            strokeWidth={1.5}
                            isAnimationActive={false}
                        />
                    )}

                    {showEMA && data[0]?.emaSlow != null && (
                        <Line
                            yAxisId="price"
                            dataKey="emaSlow"
                            stroke="#8b5cf6"
                            dot={false}
                            strokeWidth={1.5}
                            isAnimationActive={false}
                        />
                    )}

                    {lastClose != null && (
                        <ReferenceLine
                            yAxisId="price"
                            y={lastClose}
                            stroke={AXIS_COLOR}
                            strokeDasharray="2 3"
                            label={{
                                value: lastClose.toFixed(2),
                                position: "right",
                                fill: "#e6e9f0",
                                fontSize: 10
                            }}
                        />
                    )}

                    {trades.map((t, i) => (
                        <ReferenceDot
                            key={"trade-" + i}
                            yAxisId="price"
                            x={t.date}
                            y={t.price}
                            r={t.type === "ENTRY" ? 5 : 4}
                            fill={
                                t.type === "ENTRY"
                                    ? "#3b82f6"
                                    : t.type === "WIN"
                                        ? UP_COLOR
                                        : DOWN_COLOR
                            }
                            stroke="#0b0e14"
                            strokeWidth={1.5}
                        />
                    ))}

                </ComposedChart>
            </ResponsiveContainer>
        </div>
    );
}
