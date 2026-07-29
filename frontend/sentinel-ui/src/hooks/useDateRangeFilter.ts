import { useEffect, useState } from "react";
import { toDatetimeLocalValue } from "../utils/formatters";

export function useDateRangeFilter<T extends { rawDate: string }>(chart: T[]) {
    const [from, setFrom] = useState("");
    const [to, setTo] = useState("");

    useEffect(() => {
        if (chart.length) {
            setFrom(toDatetimeLocalValue(chart[0].rawDate));
            setTo(toDatetimeLocalValue(chart[chart.length - 1].rawDate));
        }
    }, [chart]);

    const fromTime = from ? new Date(from).getTime() : -Infinity;
    const toTime = to ? new Date(to).getTime() : Infinity;

    const filteredChart = chart.filter((c) => {
        const t = new Date(c.rawDate).getTime();
        return t >= fromTime && t <= toTime;
    });

    const minDate = chart.length ? toDatetimeLocalValue(chart[0].rawDate) : "";
    const maxDate = chart.length ? toDatetimeLocalValue(chart[chart.length - 1].rawDate) : "";

    function isWithinRange(rawDate: string) {
        const t = new Date(rawDate).getTime();
        return t >= fromTime && t <= toTime;
    }

    return { from, to, setFrom, setTo, filteredChart, minDate, maxDate, isWithinRange };
}
