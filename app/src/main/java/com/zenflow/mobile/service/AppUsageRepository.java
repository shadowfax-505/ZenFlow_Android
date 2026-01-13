package com.zenflow.mobile.service;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppUsageRepository {

    private final Context appContext;

    public AppUsageRepository(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public boolean hasUsageAccess() {
        try {
            AppOpsManager appOps = (AppOpsManager) appContext.getSystemService(Context.APP_OPS_SERVICE);
            if (appOps == null) return false;

            int mode;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mode = appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        android.os.Process.myUid(), appContext.getPackageName());
            } else {
                mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        android.os.Process.myUid(), appContext.getPackageName());
            }
            return mode == AppOpsManager.MODE_ALLOWED;
        } catch (Throwable t) {
            return false;
        }
    }

    public List<AppUsageItem> getTopUsageLast24h(int topN) {
        long now = System.currentTimeMillis();
        long dayAgo = now - 24L * 60L * 60L * 1000L;

        try {
            UsageStatsManager usm = (UsageStatsManager) appContext.getSystemService(Context.USAGE_STATS_SERVICE);
            if (usm == null) return new ArrayList<>();

            Map<String, UsageStats> aggregated = usm.queryAndAggregateUsageStats(dayAgo, now);
            if (aggregated == null) return new ArrayList<>();

            PackageManager pm = appContext.getPackageManager();
            Map<String, Long> totals = new HashMap<>();

            for (UsageStats u : aggregated.values()) {
                long t = u.getTotalTimeInForeground();
                if (t <= 0) continue;
                totals.merge(u.getPackageName(), t, Long::sum);
            }

            List<AppUsageItem> items = new ArrayList<>();
            for (Map.Entry<String, Long> e : totals.entrySet()) {
                String pkg = e.getKey();
                long t = e.getValue();
                String label;
                try {
                    ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
                    label = pm.getApplicationLabel(ai).toString();
                } catch (Exception ex) {
                    label = pkg;
                }
                items.add(new AppUsageItem(pkg, label, t));
            }

            items.sort(Comparator.comparingLong((AppUsageItem i) -> i.timeForegroundMs).reversed());
            if (topN > 0 && items.size() > topN) {
                items = new ArrayList<>(items.subList(0, topN));
            }
            return items;
        } catch (SecurityException se) {
            return new ArrayList<>();
        } catch (Throwable t) {
            return new ArrayList<>();
        }
    }
}
