package com.zenflow.mobile;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.zenflow.mobile.analytics.AnalyticsLogger;
import com.zenflow.mobile.data.AppDatabase;
import com.zenflow.mobile.data.SessionEntity;
import com.zenflow.mobile.service.AppUsageItem;
import com.zenflow.mobile.service.UsageCategory;
import com.zenflow.mobile.service.UsageCategoryPolicy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardActivity extends AppCompatActivity {

    private TextView totalSessionsText;
    private TextView completedSessionsText;
    private LinearLayout focusChart;
    private LinearLayout appUsageChart;
    private PieChart appUsagePie;
    private LinearLayout appUsageLegend;

    private final Handler refreshHandler = new Handler(Looper.getMainLooper());
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            loadSessionStats();
            viewModel.refresh();
            refreshHandler.postDelayed(this, 60_000);
        }
    };

    private DashboardViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        totalSessionsText = findViewById(R.id.totalSessions);
        completedSessionsText = findViewById(R.id.completedSessions);
        focusChart = findViewById(R.id.focusChart);
        appUsageChart = findViewById(R.id.appUsageChart);
        appUsagePie = findViewById(R.id.appUsagePie);
        appUsageLegend = findViewById(R.id.appUsageLegend);

        setupPieChart();

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        viewModel.getHasUsageAccess().observe(this, allowed -> {
            if (allowed == null) return;
            if (!allowed) showUsagePermissionRow();
        });
        viewModel.getTopApps().observe(this, this::renderAppUsagePie);

        loadSessionStats();
        viewModel.refresh();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshHandler.removeCallbacks(refreshRunnable);
        refreshHandler.postDelayed(refreshRunnable, 60_000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        refreshHandler.removeCallbacks(refreshRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsLogger.logScreenView(this, "Dashboard", getClass().getSimpleName());
    }

    private void loadSessionStats() {
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

            runOnUiThread(() -> {
                totalSessionsText.setText("Last 24h Sessions: " + total);
                completedSessionsText.setText("Completed: " + completed);
                renderFocusChart(buckets);
            });
        }).start();
    }

    private void setupPieChart() {
        appUsagePie.setUsePercentValues(true);
        appUsagePie.getDescription().setEnabled(false);
        appUsagePie.setDrawEntryLabels(false);
        appUsagePie.setHoleRadius(55f);
        appUsagePie.setTransparentCircleRadius(60f);
        appUsagePie.getLegend().setEnabled(false);
        appUsagePie.setNoDataText("No usage data. Check permissions.");
        appUsagePie.setNoDataTextColor(0xFFAAAAAA);
    }

    private void showUsagePermissionRow() {
        appUsageLegend.removeAllViews();
        appUsageChart.removeAllViews();

        TextView msg = new TextView(this);
        msg.setText("Usage access is required to show app usage. Tap to enable.");
        msg.setTextColor(0xFFAAAAAA);
        msg.setPadding(0, dpToPx(8), 0, dpToPx(8));
        msg.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)));
        appUsageLegend.addView(msg);

        appUsagePie.clear();
        appUsagePie.invalidate();
    }

    private void renderAppUsagePie(List<AppUsageItem> usage) {
        appUsageLegend.removeAllViews();
        appUsageChart.removeAllViews();

        if (usage == null || usage.isEmpty()) {
            TextView none = new TextView(this);
            none.setText("No usage data. Check permissions.");
            none.setTextColor(0xFFAAAAAA);
            appUsageLegend.addView(none);
            appUsagePie.clear();
            appUsagePie.invalidate();
            return;
        }

        int cutBackMin = SettingsStore.getUsageCutBackMinutes(this);
        int seriouslyMin = SettingsStore.getUsageSeriouslyAddictedMinutes(this);
        UsageCategoryPolicy policy = new UsageCategoryPolicy(cutBackMin, seriouslyMin);

        long totalMs = 0;
        for (AppUsageItem i : usage) totalMs += i.timeForegroundMs;
        if (totalMs <= 0) totalMs = 1;

        List<PieEntry> entries = new ArrayList<>();
        for (AppUsageItem i : usage) {
            float pct = (i.timeForegroundMs * 100f) / (float) totalMs;
            if (pct < 0.5f) continue;
            entries.add(new PieEntry(pct, i.appLabel));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(2f);
        dataSet.setColors(new int[]{
                0xFF42A5F5,
                0xFF66BB6A,
                0xFFFFCA28,
                0xFFEF5350,
                0xFFAB47BC,
                0xFF26C6DA,
                0xFF8D6E63,
                0xFF7E57C2
        });
        dataSet.setValueTextColor(0xFFFFFFFF);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(appUsagePie));

        appUsagePie.setData(data);
        appUsagePie.invalidate();

        for (AppUsageItem i : usage) {
            long mins = i.timeForegroundMs / 60000;
            if (mins <= 0) continue;

            UsageCategory cat = policy.categorize(i.timeForegroundMs);

            TextView row = new TextView(this);
            row.setText(i.appLabel + ": " + mins + " mins" + "  \u2022  " + cat.label);
            row.setTextColor(cat == UsageCategory.SERIOUSLY_ADDICTED ? 0xFFFF5252
                    : (cat == UsageCategory.CUT_BACK ? 0xFFFFCA28 : 0xFFFFFFFF));
            row.setPadding(0, dpToPx(6), 0, dpToPx(6));
            appUsageLegend.addView(row);
        }
    }

    private void renderFocusChart(long[] buckets) {
        focusChart.removeAllViews();
        long max = 0;
        for (long b : buckets) max = Math.max(max, b);
        if (max == 0) max = 1;

        for (long b : buckets) {
            View bar = new View(this);
            int height = (int) ((b * 150.0) / max);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dpToPx(height), 1f);
            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            bar.setLayoutParams(params);
            bar.setBackgroundColor(0xFF4CAF50);
            focusChart.addView(bar);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
