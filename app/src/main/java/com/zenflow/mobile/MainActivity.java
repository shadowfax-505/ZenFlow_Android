package com.zenflow.mobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zenflow.mobile.analytics.AnalyticsLogger;
import com.zenflow.mobile.auth.SessionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        if (!SessionManager.isLoggedIn(this)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        findViewById(R.id.btnFocus).setOnClickListener(v ->
                startActivity(new Intent(this, FocusActivity.class)));

        findViewById(R.id.btnDashboard).setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));

        findViewById(R.id.btnHistory).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));

        findViewById(R.id.btnReminders).setOnClickListener(v ->
                startActivity(new Intent(this, RemindersActivity.class)));

        findViewById(R.id.btnSettings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsLogger.logScreenView(this, "Main", getClass().getSimpleName());
    }
}
