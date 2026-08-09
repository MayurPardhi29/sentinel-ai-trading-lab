package com.sentinel.strategy_engine.util;

import java.util.Map;

public final class IntervalFormatter {

    private static final Map<String, String> LABELS = Map.ofEntries(
            Map.entry("1min", "1-minute"),
            Map.entry("5min", "5-minute"),
            Map.entry("15min", "15-minute"),
            Map.entry("30min", "30-minute"),
            Map.entry("45min", "45-minute"),
            Map.entry("1h", "1-hour"),
            Map.entry("2h", "2-hour"),
            Map.entry("4h", "4-hour"),
            Map.entry("8h", "8-hour"),
            Map.entry("1day", "daily"),
            Map.entry("1week", "weekly"),
            Map.entry("1month", "monthly")
    );

    private IntervalFormatter() {
    }

    public static String label(String interval) {
        if (interval == null) {
            return "unknown-interval";
        }
        return LABELS.getOrDefault(interval, interval);
    }

    /**
     * @param interval       raw interval code, e.g. "15min"
     * @param actionable     whether the recommendation is one you'd actually act on
     *                       (e.g. STRONG BUY / HIGH CONFIDENCE), as opposed to WAIT/NO SIGNAL
     */
    public static String entryHint(String interval, boolean actionable) {
        if (!actionable) {
            return "No entry — signal not confirmed on the " + label(interval) + " timeframe.";
        }
        return "Entry: next " + label(interval) + " candle open.";
    }
}
