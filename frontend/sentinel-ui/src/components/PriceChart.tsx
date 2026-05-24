import {

    ResponsiveContainer,
    
    ComposedChart,
    
    Bar,
    
    Line,
    
    XAxis,
    
    YAxis,
    
    Tooltip,
    
    CartesianGrid
    
    }
    
    from "recharts";
    
    interface Props {
    
    data:any[];
    
    }
    
    export default function PriceChart({
    
    data
    
    }:Props){
    
    return(
    
    <div
    className="chart"
    >
    
    <ResponsiveContainer>
    
    <ComposedChart
    data={data}
    >
    
    <CartesianGrid
    stroke="#1f2937"
    />
    
    <XAxis
    
    dataKey="date"
    
    tick={{
    fill:"#94a3b8"
    }}
    
    />
    
    <YAxis
    
    domain={
    ["auto","auto"]
    }
    
    tick={{
    fill:"#94a3b8"
    }}
    
    />
    
    <Tooltip/>
    
    <Bar
    
    dataKey=
    "volume"
    
    fill=
    "#334155"
    
    yAxisId=
    "volume"
    
    />
    
    <Line
    
    dataKey=
    "close"
    
    stroke=
    "#e2e8f0"
    
    dot=
    {false}
    
    strokeWidth=
    {2}
    
    />
    
    {
    
    data[0]
    ?.emaFast
    
    &&
    
    <Line
    
    dataKey=
    "emaFast"
    
    stroke=
    "#f59e0b"
    
    dot=
    {false}
    
    />
    
    }
    
    {
    
    data[0]
    ?.emaSlow
    
    &&
    
    <Line
    
    dataKey=
    "emaSlow"
    
    stroke=
    "#22c55e"
    
    dot=
    {false}
    
    />
    
    }
    
    </ComposedChart>
    
    </ResponsiveContainer>
    
    </div>
    
    );
    
    }