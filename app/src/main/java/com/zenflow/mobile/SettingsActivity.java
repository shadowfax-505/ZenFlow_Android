package com.zenflow.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

        int savedMinutes = SettingsStore.getFocusDurationMinutes(this);
        focusSeek.setProgress(savedMinutes);
        focusDurationLabel.setText(savedMinutes + " minutes");

        focusSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int displayProgress = Math.max(1, progress);
                focusDurationLabel.setText(displayProgress + " minutes");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        switchAccount.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) return;

            SessionManager.logout(this);

            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        saveButton.setOnClickListener(v -> {
            int minutes = Math.max(1, focusSeek.getProgress());
            SettingsStore.setFocusDurationMinutes(this, minutes);
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
