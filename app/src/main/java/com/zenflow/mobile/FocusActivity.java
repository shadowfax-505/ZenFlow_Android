package com.zenflow.mobile;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.zenflow.mobile.databinding.ActivityFocusBinding;

public class FocusActivity extends AppCompatActivity {

    private ActivityFocusBinding binding;
    private FocusViewModel viewModel;

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
    }
}
