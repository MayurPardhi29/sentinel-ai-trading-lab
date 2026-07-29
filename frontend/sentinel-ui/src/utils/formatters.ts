export function formatChartDate(raw: string, timeframe: string): string {
    const isIntraday = timeframe.includes("min") || timeframe.includes("h");

    if (isIntraday) {
        return new Date(raw).toLocaleTimeString("en-IN", {
            hour: "2-digit",
            minute: "2-digit",
            hour12: false
        });
    }

    return new Date(raw).toLocaleDateString("en-IN", { day: "numeric", month: "short" });
}

export function formatVolume(value: number): string {
    if (value == null || Number.isNaN(value)) {
        return "-";
    }

    const abs = Math.abs(value);

    if (abs >= 1_000_000_000) {
        return (value / 1_000_000_000).toFixed(2) + "B";
    }
    if (abs >= 1_000_000) {
        return (value / 1_000_000).toFixed(2) + "M";
    }
    if (abs >= 1_000) {
        return (value / 1_000).toFixed(1) + "K";
    }

    return Math.round(value).toString();
}

// Converts an ISO-ish datetime string into the value <input type="datetime-local">
// expects ("YYYY-MM-DDTHH:mm").
export function toDatetimeLocalValue(raw: string): string {
    const d = new Date(raw);
    if (Number.isNaN(d.getTime())) {
        return "";
    }
    const pad = (n: number) => String(n).padStart(2, "0");
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
}
