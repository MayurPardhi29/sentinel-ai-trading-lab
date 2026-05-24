import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    Tooltip,
    ResponsiveContainer
} from "recharts";

interface Props {
    data: any[];
}

export default function PriceChart({
    data
}: Props) {

    return (

        <div
            style={{
                width: "100%",
                height: 350
            }}
        >

            <ResponsiveContainer>

            <LineChart data={data} >

                <XAxis
                    dataKey="datetime"
                />

                <YAxis/>

                <Tooltip/>

                <Line
                    type="monotone"
                    dataKey="close"
                    dot={false}
                />

                <Line
                    type="monotone"
                    dataKey="emaFast"
                    dot={false}
                />

                <Line
                    type="monotone"
                    dataKey="emaSlow"
                    dot={false}
                />

            </LineChart>

            </ResponsiveContainer>

        </div>

    );

}