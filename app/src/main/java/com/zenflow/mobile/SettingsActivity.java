package com.zenflow.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zenflow.mobile.analytics.AnalyticsLogger;
import com.zenflow.mobile.auth.SessionManager;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView focusDurationLabel = findViewById(R.id.focusDurationLabel);
        SeekBar focusSeek = findViewById(R.id.focusDurationSeek);
        Button saveButton = findViewById(R.id.saveSettingsBtn);
        Switch switchAccount = findViewById(R.id.switchAccount);

        TextView cutBackLabel = findViewById(R.id.cutBackLabel);
        SeekBar cutBackSeek = findViewById(R.id.cutBackSeek);
        TextView seriouslyAddictedLabel = findViewById(R.id.seriouslyAddictedLabel);
        SeekBar seriouslyAddictedSeek = findViewById(R.id.seriouslyAddictedSeek);

        int savedMinutes = SettingsStore.getFocusDurationMinutes(this);
        focusSeek.setProgress(savedMinutes);
        focusDurationLabel.setText(savedMinutes + " minutes");

        int cutBackMin = SettingsStore.getUsageCutBackMinutes(this);
        int seriouslyMin = SettingsStore.getUsageSeriouslyAddictedMinutes(this);
        cutBackSeek.setProgress(cutBackMin);
        seriouslyAddictedSeek.setProgress(seriouslyMin);
        cutBackLabel.setText(cutBackMin + " minutes");
        seriouslyAddictedLabel.setText(seriouslyMin + " minutes");

        focusSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int minutes = Math.max(1, progress);
                focusDurationLabel.setText(minutes + " minutes");
                if (fromUser) {
                    SettingsStore.setFocusDurationMinutes(SettingsActivity.this, minutes);
                    AnalyticsLogger.logSettingsChanged(SettingsActivity.this, SettingsStore.KEY_FOCUS_DURATION_MIN);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        cutBackSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int minutes = Math.max(1, progress);
                cutBackLabel.setText(minutes + " minutes");
                if (fromUser) {
                    SettingsStore.setUsageCutBackMinutes(SettingsActivity.this, minutes);
                    AnalyticsLogger.logSettingsChanged(SettingsActivity.this, SettingsStore.KEY_USAGE_CUT_BACK_MIN);

                    int currentSeriously = SettingsStore.getUsageSeriouslyAddictedMinutes(SettingsActivity.this);
                    if (currentSeriously < minutes) {
                        SettingsStore.setUsageSeriouslyAddictedMinutes(SettingsActivity.this, minutes);
                        seriouslyAddictedSeek.setProgress(minutes);
                        seriouslyAddictedLabel.setText(minutes + " minutes");
                        AnalyticsLogger.logSettingsChanged(SettingsActivity.this, SettingsStore.KEY_USAGE_SERIOUSLY_ADDICTED_MIN);
                    }
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        seriouslyAddictedSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int cutBack = Math.max(1, cutBackSeek.getProgress());
                int minutes = Math.max(cutBack, progress);
                if (minutes != progress) {
                    seriouslyAddictedSeek.setProgress(minutes);
                }
                seriouslyAddictedLabel.setText(minutes + " minutes");
                if (fromUser) {
                    SettingsStore.setUsageSeriouslyAddictedMinutes(SettingsActivity.this, minutes);
                    AnalyticsLogger.logSettingsChanged(SettingsActivity.this, SettingsStore.KEY_USAGE_SERIOUSLY_ADDICTED_MIN);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        switchAccount.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) return;

            AnalyticsLogger.logSettingsChanged(this, "switch_account");
            SessionManager.logout(this);

            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        saveButton.setOnClickListener(v -> {
            int minutes = Math.max(1, focusSeek.getProgress());
            SettingsStore.setFocusDurationMinutes(this, minutes);

            int cutBack = Math.max(1, cutBackSeek.getProgress());
            SettingsStore.setUsageCutBackMinutes(this, cutBack);

            int seriously = Math.max(cutBack, seriouslyAddictedSeek.getProgress());
            SettingsStore.setUsageSeriouslyAddictedMinutes(this, seriously);

            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsLogger.logScreenView(this, "Settings", getClass().getSimpleName());
    }
}
