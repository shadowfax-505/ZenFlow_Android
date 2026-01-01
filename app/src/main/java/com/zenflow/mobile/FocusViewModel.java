package com.zenflow.mobile;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.zenflow.mobile.data.AppDatabase;
import com.zenflow.mobile.data.SessionEntity;

import android.os.CountDownTimer;

public class FocusViewModel extends AndroidViewModel {

    private MutableLiveData<String> _timerText = new MutableLiveData<>();
    private MutableLiveData<Boolean> _isRunning = new MutableLiveData<>(false);

    private long remainingTimeMs = 25 * 60 * 1000L;
    private long durationMs = 25 * 60 * 1000L;
    private CountDownTimer timer;
    private long startTs;

    public FocusViewModel(@NonNull Application application) {
        super(application);
        updateTimerText(remainingTimeMs);
    }

    public LiveData<String> getTimerText() {
        return _timerText;
    }

    public LiveData<Boolean> isRunning() {
        return _isRunning;
    }

    public void startOrResume() {
        if (_isRunning.getValue() == Boolean.TRUE) return;
        int minutes = SettingsStore.getFocusDurationMinutes(getApplication());
        durationMs = minutes * 60 * 1000L;
        if (remainingTimeMs > durationMs) {
            remainingTimeMs = durationMs;
        }

        startTimer(remainingTimeMs);
        _isRunning.setValue(true);
        startTs = System.currentTimeMillis();
    }

    public void pause() {
        if (_isRunning.getValue() == Boolean.FALSE) return;

        if (timer != null) {
            timer.cancel();
        }
        _isRunning.setValue(false);
    }

    public void reset() {
        pause();
        remainingTimeMs = durationMs;
        updateTimerText(remainingTimeMs);
    }

    private void startTimer(long millisInFuture) {
        timer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTimeMs = millisUntilFinished;
                updateTimerText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                updateTimerText(0);
                _isRunning.setValue(false);
                saveSession(true);
            }
        }.start();
    }

    private void updateTimerText(long millis) {
        int minutes = (int) (millis / 60000);
        int seconds = (int) (millis / 1000) % 60;
        _timerText.postValue(String.format("%02d:%02d", minutes, seconds));
    }

    private void saveSession(boolean completed) {
        new Thread(() -> {
            SessionEntity session = new SessionEntity();
            session.startTs = startTs;
            session.endTs = System.currentTimeMillis();
            session.type = "FOCUS";
            session.completed = completed;
            AppDatabase.get(getApplication()).sessionDao().insert(session);
        }).start();
    }

    public void setDurationMinutes(int minutes) {
        durationMs = minutes * 60 * 1000L;
        if (_isRunning.getValue() == Boolean.FALSE) {
             remainingTimeMs = durationMs;
             updateTimerText(remainingTimeMs);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (timer != null) timer.cancel();
    }
}
