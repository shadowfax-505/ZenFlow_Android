package com.zenflow.mobile;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.zenflow.mobile.analytics.AnalyticsLogger;
import com.zenflow.mobile.databinding.ActivityFocusBinding;

public class FocusActivity extends AppCompatActivity {

    private ActivityFocusBinding binding;
    private FocusViewModel viewModel;

    private final SharedPreferences.OnSharedPreferenceChangeListener prefListener = (prefs, key) -> {
        if (SettingsStore.KEY_FOCUS_DURATION_MIN.equals(key)) {
            int minutes = SettingsStore.getFocusDurationMinutes(this);
            viewModel.setDurationMinutes(minutes);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFocusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(FocusViewModel.class);

        viewModel.getTimerText().observe(this,
                text -> binding.timerText.setText(text));

        viewModel.isRunning().observe(this, running ->
                binding.pauseResumeBtn.setText(running ? "Pause" : "Start"));

        binding.pauseResumeBtn.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(viewModel.isRunning().getValue())) {
                viewModel.pause();
            } else {
                viewModel.startOrResume();
            }
        });

        binding.resetBtn.setOnClickListener(v -> viewModel.reset());

        int minutes = SettingsStore.getFocusDurationMinutes(this);
        viewModel.setDurationMinutes(minutes);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SettingsStore.prefs(this).registerOnSharedPreferenceChangeListener(prefListener);
    }

    @Override
    protected void onStop() {
        SettingsStore.prefs(this).unregisterOnSharedPreferenceChangeListener(prefListener);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsLogger.logScreenView(this, "Focus", getClass().getSimpleName());
    }
}
