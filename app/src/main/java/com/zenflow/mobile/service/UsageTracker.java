package com.zenflow.mobile.service;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import java.util.List;

public class UsageTracker {

    public static List<UsageStats> getStats(Context c) {
        UsageStatsManager m =
                (UsageStatsManager) c.getSystemService(Context.USAGE_STATS_SERVICE);

        long end = System.currentTimeMillis();
        long start = end - 60_000;

        return m.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                start,
                end
        );
    }
}
