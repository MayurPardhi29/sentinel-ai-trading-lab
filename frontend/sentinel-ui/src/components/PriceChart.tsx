import {

    ResponsiveContainer,

    ComposedChart,

    Bar,

    Line,

    XAxis,

    YAxis,

    Tooltip,

    CartesianGrid,

    ReferenceDot

}

    from "recharts";

interface Props {

    data: any[];

    showPrice: boolean;

    showEMA: boolean;

    showVolume: boolean;

}

export default function PriceChart({

    data,

    showPrice,

    showEMA,

    showVolume

}: Props) {

    return (

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
                            fill: "#94a3b8"
                        }}

                    />

                    <YAxis

                        yAxisId=
                        "price"

                        domain={
                            [
                                "auto",
                                "auto"
                            ]
                        }

                        tick={{
                            fill: "#94a3b8"
                        }}

                    />

                    <YAxis

                        yAxisId=
                        "volume"

                        orientation=
                        "right"

                        hide={
                            !showVolume
                        }

                    />

<Tooltip

contentStyle={{

background:
"#0f172a",

border:
"1px solid #1e293b",

borderRadius:
16,

color:
"#f8fafc"

}}

labelStyle={{

color:
"#94a3b8"

}}

itemStyle={{

color:
"#f8fafc"

}}

cursor={{

stroke:
"#334155",

strokeWidth:
1

}}

/>

                    {

                        showVolume

                        &&

                        <Bar

                            yAxisId=
                            "volume"

                            dataKey=
                            "volume"

                            fill=
                            "#334155"

                            opacity={
                                0.25
                            }

                        />

                    }

                    {

                        showPrice

                        &&

                        <Line

                            yAxisId=
                            "price"

                            dataKey=
                            "close"

                            stroke="#e2e8f0"

                            dot={false}

                            strokeWidth={2}

                        />

                    }

                    {

                        showEMA

                        &&

                        data[0]?.emaFast

                        &&

                        <Line

                            yAxisId= "price"
                            dataKey="emaFast"

                            stroke="#f59e0b"

                            dot={false}

                        />

                    }

                    {

                        showEMA

                        &&

                        data[0]?.emaSlow

                        &&

                        <Line

                            yAxisId= "price"
                            dataKey="emaSlow"

                            stroke="#22c55e"

                            dot={false}

                        />

                    }

                    {

                        data

                            .filter(

                                d =>

                                    d.signal

                            )

                            .map(

                                (

                                    d,

                                    i

                                ) => (

                                    <ReferenceDot

                                        key=
                                        {i}

                                        x=
                                        {d.date}

                                        y=
                                        {d.close}

                                        r=
                                        {6}

                                        fill=
                                        "#22c55e"

                                    />

                                )

                            )

                    }

                </ComposedChart>

            </ResponsiveContainer>

        </div>

    );

}