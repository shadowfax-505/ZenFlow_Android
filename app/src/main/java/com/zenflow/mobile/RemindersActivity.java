package com.zenflow.mobile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zenflow.mobile.analytics.AnalyticsLogger;
import com.zenflow.mobile.data.AppDatabase;
import com.zenflow.mobile.data.ReminderEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RemindersActivity extends AppCompatActivity {

    private TextView selectedDateText;
    private Button pickDateButton;
    private EditText reminderInput;
    private Button addReminderButton;

    private LinearLayout remindersForDateContainer;
    private LinearLayout upcomingRemindersContainer;

    private final Calendar selectedDay = Calendar.getInstance();
    private final SimpleDateFormat dayFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        selectedDateText = findViewById(R.id.selectedDateText);
        pickDateButton = findViewById(R.id.btnPickDate);
        reminderInput = findViewById(R.id.reminderInput);
        addReminderButton = findViewById(R.id.btnAddReminder);
        remindersForDateContainer = findViewById(R.id.remindersForDateContainer);
        upcomingRemindersContainer = findViewById(R.id.upcomingRemindersContainer);

        updateSelectedDateLabel();

        pickDateButton.setOnClickListener(v -> openDatePicker());
        addReminderButton.setOnClickListener(v -> addReminder());

        refreshLists();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsLogger.logScreenView(this, "Reminders", getClass().getSimpleName());
        refreshLists();
    }

    private void openDatePicker() {
        int y = selectedDay.get(Calendar.YEAR);
        int m = selectedDay.get(Calendar.MONTH);
        int d = selectedDay.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDay.set(Calendar.YEAR, year);
            selectedDay.set(Calendar.MONTH, month);
            selectedDay.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            selectedDay.set(Calendar.HOUR_OF_DAY, 0);
            selectedDay.set(Calendar.MINUTE, 0);
            selectedDay.set(Calendar.SECOND, 0);
            selectedDay.set(Calendar.MILLISECOND, 0);
            updateSelectedDateLabel();
            refreshLists();
        }, y, m, d);
        dialog.show();
    }

    private void updateSelectedDateLabel() {
        if (selectedDateText == null) return;
        selectedDateText.setText("Selected: " + dayFmt.format(selectedDay.getTime()));
    }

    private long selectedEpochDay() {
        long ms = selectedDay.getTimeInMillis();
        return ms / 86_400_000L;
    }

    private long epochDayNow() {
        return System.currentTimeMillis() / 86_400_000L;
    }

    private void addReminder() {
        if (reminderInput == null) return;
        String text = reminderInput.getText() == null ? "" : reminderInput.getText().toString().trim();
        if (text.isEmpty()) return;

        long epochDay = selectedEpochDay();

        new Thread(() -> {
            try {
                ReminderEntity r = new ReminderEntity();
                r.text = text;
                r.dateEpochDay = epochDay;
                r.createdTs = System.currentTimeMillis();
                AppDatabase.get(this).reminderDao().insert(r);
            } catch (Throwable ignored) {
            }

            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                try {
                    reminderInput.setText("");
                    refreshLists();
                } catch (Throwable ignored) {
                }
            });
        }).start();
    }

    private void refreshLists() {
        final long selected = selectedEpochDay();
        final long start = epochDayNow();
        final long end = start + 3;

        new Thread(() -> {
            List<ReminderEntity> forDay;
            List<ReminderEntity> upcoming;
            try {
                forDay = AppDatabase.get(this).reminderDao().getByEpochDay(selected);
            } catch (Throwable t) {
                forDay = null;
            }
            try {
                upcoming = AppDatabase.get(this).reminderDao().getUpcoming(start, end);
            } catch (Throwable t) {
                upcoming = null;
            }

            final List<ReminderEntity> finalForDay = forDay;
            final List<ReminderEntity> finalUpcoming = upcoming;

            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                try {
                    if (remindersForDateContainer != null) {
                        renderReminderList(remindersForDateContainer, finalForDay, selected);
                    }
                    if (upcomingRemindersContainer != null) {
                        renderUpcoming(finalUpcoming);
                    }
                } catch (Throwable ignored) {
                }
            });
        }).start();
    }


    private void renderReminderList(LinearLayout container, List<ReminderEntity> list, long epochDay) {
        if (container == null || isFinishing() || isDestroyed()) return;
        container.removeAllViews();
        if (list == null || list.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No reminders for this date.");
            empty.setTextColor(0xFFAAAAAA);
            container.addView(empty);
            return;
        }

        for (ReminderEntity r : list) {
            if (r == null) continue;
            TextView row = new TextView(this);
            row.setText("• " + r.text);
            row.setTextColor(0xFFFFFFFF);
            row.setPadding(0, dpToPx(6), 0, dpToPx(6));
            container.addView(row);
        }
    }

    private void renderUpcoming(List<ReminderEntity> list) {
        if (upcomingRemindersContainer == null || isFinishing() || isDestroyed()) return;
        upcomingRemindersContainer.removeAllViews();
        if (list == null || list.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No upcoming reminders in the next 3 days.");
            empty.setTextColor(0xFFAAAAAA);
            upcomingRemindersContainer.addView(empty);
            return;
        }

        long todayEpochDay = epochDayNow();

        for (ReminderEntity r : list) {
            if (r == null) continue;
            Date when = new Date(r.dateEpochDay * 86_400_000L);
            String day = dayFmt.format(when);
            long delta = r.dateEpochDay - todayEpochDay;

            TextView row = new TextView(this);
            row.setText(day + " (in " + delta + "d): " + r.text);
            row.setTextColor(0xFFDDDDDD);
            row.setPadding(0, dpToPx(6), 0, dpToPx(6));
            upcomingRemindersContainer.addView(row);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
