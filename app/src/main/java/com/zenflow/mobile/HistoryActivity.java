package com.zenflow.mobile;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zenflow.mobile.analytics.AnalyticsLogger;
import com.zenflow.mobile.data.AppDatabase;
import com.zenflow.mobile.data.SessionEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout historyContainer;

    private final SimpleDateFormat dayFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyContainer = findViewById(R.id.historyContainer);

        loadHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsLogger.logScreenView(this, "History", getClass().getSimpleName());
        loadHistory();
    }

    private void loadHistory() {
        new Thread(() -> {
            List<SessionEntity> sessions;
            try {
                sessions = AppDatabase.get(this).sessionDao().getAll();
            } catch (Throwable t) {
                sessions = null;
            }

            Map<String, List<SessionEntity>> byDay = new LinkedHashMap<>();
            if (sessions != null) {
                for (SessionEntity s : sessions) {
                    if (s == null) continue;
                    String day = dayFmt.format(new Date(s.startTs));
                    List<SessionEntity> bucket = byDay.get(day);
                    if (bucket == null) {
                        bucket = new ArrayList<>();
                        byDay.put(day, bucket);
                    }
                    bucket.add(s);
                }
            }

            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                try {
                    render(byDay);
                } catch (Throwable ignored) {
                }
            });
        }).start();
    }

    private void render(Map<String, List<SessionEntity>> byDay) {
        if (historyContainer == null) return;
        historyContainer.removeAllViews();

        if (byDay.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No history yet.");
            empty.setTextColor(0xFFAAAAAA);
            historyContainer.addView(empty);
            return;
        }

        for (Map.Entry<String, List<SessionEntity>> entry : byDay.entrySet()) {
            TextView header = new TextView(this);
            header.setText(entry.getKey());
            header.setTextColor(0xFFFFFFFF);
            header.setTextSize(18f);
            header.setPadding(0, dpToPx(16), 0, dpToPx(8));
            historyContainer.addView(header);

            for (SessionEntity s : entry.getValue()) {
                String start = timeFmt.format(new Date(s.startTs));
                String end = s.endTs == null ? "--:--" : timeFmt.format(new Date(s.endTs));
                String type = s.type == null ? "SESSION" : s.type;

                TextView row = new TextView(this);
                row.setText(type + ": " + start + " - " + end);
                row.setTextColor(0xFFDDDDDD);
                row.setPadding(0, dpToPx(4), 0, dpToPx(4));
                historyContainer.addView(row);
            }
        }

        ViewGroup.LayoutParams lp = historyContainer.getLayoutParams();
        if (lp != null) {
            historyContainer.setLayoutParams(lp);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
