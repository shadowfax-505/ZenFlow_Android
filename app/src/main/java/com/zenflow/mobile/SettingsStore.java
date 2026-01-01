package com.zenflow.mobile;

import android.content.Context;
import android.content.SharedPreferences;


public final class SettingsStore {

    private static final String PREFS_NAME = "zenflow_settings";
    private static final String KEY_FOCUS_DURATION_MIN = "focus_duration_min";
    private static final int DEFAULT_FOCUS_DURATION_MIN = 25;

    private SettingsStore() {

    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static int getFocusDurationMinutes(Context context) {
        return prefs(context).getInt(KEY_FOCUS_DURATION_MIN, DEFAULT_FOCUS_DURATION_MIN);
    }

    public static void setFocusDurationMinutes(Context context, int minutes) {
        prefs(context).edit().putInt(KEY_FOCUS_DURATION_MIN, minutes).apply();
    }
}

