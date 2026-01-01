package com.zenflow.mobile;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.zenflow.mobile.data.AppDatabase;
import com.zenflow.mobile.data.SessionEntity;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class DashboardActivity extends AppCompatActivity {

    private TextView totalSessionsText;
    private TextView completedSessionsText;
    private LinearLayout focusChart;
    private LinearLayout appUsageChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        totalSessionsText = findViewById(R.id.totalSessions);
        completedSessionsText = findViewById(R.id.completedSessions);
        focusChart = findViewById(R.id.focusChart);
        appUsageChart = findViewById(R.id.appUsageChart);

        loadStats();
    }

    private void loadStats() {
        new Thread(() -> {
            long now = System.currentTimeMillis();
            long dayAgo = now - (24 * 60 * 60 * 1000);

            List<SessionEntity> sessions = AppDatabase.get(this).sessionDao().getAll();
            List<SessionEntity> last24hSessions = sessions.stream()
                    .filter(s -> s.startTs >= dayAgo)
                    .collect(Collectors.toList());

            long total = last24hSessions.size();
            long completed = last24hSessions.stream().filter(s -> s.completed).count();

            long[] buckets = new long[6];
            for (SessionEntity s : last24hSessions) {
                if (s.endTs == null) continue;
                int bucketIdx = (int) ((s.startTs - dayAgo) / (4 * 60 * 60 * 1000));
                if (bucketIdx >= 0 && bucketIdx < 6) {
                    buckets[bucketIdx] += (s.endTs - s.startTs);
                }
            }

            UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            Map<String, UsageStats> usageStats = usm.queryAndAggregateUsageStats(dayAgo, now);
            
            List<UsageStats> sortedUsage = usageStats.values().stream()
                    .filter(u -> u.getTotalTimeInForeground() > 0)
                    .sorted(Comparator.comparingLong(UsageStats::getTotalTimeInForeground).reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            runOnUiThread(() -> {
                totalSessionsText.setText("Last 24h Sessions: " + total);
                completedSessionsText.setText("Completed: " + completed);
                renderFocusChart(buckets);
                renderAppUsageChart(sortedUsage);
            });
        }).start();
    }

    private void renderFocusChart(long[] buckets) {
        focusChart.removeAllViews();
        long max = 0;
        for (long b : buckets) max = Math.max(max, b);
        if (max == 0) max = 1;

        for (long b : buckets) {
            View bar = new View(this);
            int height = (int) ((b * 150.0) / max); // scale to max 150dp approx
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dpToPx(height), 1f);
            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            bar.setLayoutParams(params);
            bar.setBackgroundColor(0xFF4CAF50);
            focusChart.addView(bar);
        }
    }

    private void renderAppUsageChart(List<UsageStats> usage) {
        appUsageChart.removeAllViews();
        PackageManager pm = getPackageManager();

        for (UsageStats u : usage) {
            String label;
            try {
                ApplicationInfo ai = pm.getApplicationInfo(u.getPackageName(), 0);
                label = pm.getApplicationLabel(ai).toString();
            } catch (Exception e) {
                label = u.getPackageName();
            }

            long mins = u.getTotalTimeInForeground() / 60000;
            if (mins == 0) continue;

            TextView row = new TextView(this);
            row.setText(label + ": " + mins + " mins");
            row.setTextColor(0xFFFFFFFF);
            row.setPadding(0, dpToPx(8), 0, dpToPx(8));
            appUsageChart.addView(row);

            View bar = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(4));
            bar.setLayoutParams(params);
            bar.setBackgroundColor(0xFF2196F3);
            appUsageChart.addView(bar);
        }
        
        if (usage.isEmpty()) {
            TextView none = new TextView(this);
            none.setText("No usage data. Check permissions.");
            none.setTextColor(0xFFAAAAAA);
            appUsageChart.addView(none);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
