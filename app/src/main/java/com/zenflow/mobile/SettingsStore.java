package com.zenflow.mobile;

import android.content.Context;
import android.content.SharedPreferences;


public final class SettingsStore {

    public static final String PREFS_NAME = "zenflow_settings";
    public static final String KEY_FOCUS_DURATION_MIN = "focus_duration_min";
    private static final int DEFAULT_FOCUS_DURATION_MIN = 25;

    public static final String KEY_USAGE_CUT_BACK_MIN = "usage_cut_back_min";
    public static final String KEY_USAGE_SERIOUSLY_ADDICTED_MIN = "usage_seriously_addicted_min";
    private static final int DEFAULT_USAGE_CUT_BACK_MIN = 60;
    private static final int DEFAULT_USAGE_SERIOUSLY_ADDICTED_MIN = 120;

    private SettingsStore() {

    }

    public static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static int getFocusDurationMinutes(Context context) {
        return prefs(context).getInt(KEY_FOCUS_DURATION_MIN, DEFAULT_FOCUS_DURATION_MIN);
    }

    public static void setFocusDurationMinutes(Context context, int minutes) {
        prefs(context).edit().putInt(KEY_FOCUS_DURATION_MIN, minutes).apply();
    }

    public static int getUsageCutBackMinutes(Context context) {
        return prefs(context).getInt(KEY_USAGE_CUT_BACK_MIN, DEFAULT_USAGE_CUT_BACK_MIN);
    }

    public static void setUsageCutBackMinutes(Context context, int minutes) {
        prefs(context).edit().putInt(KEY_USAGE_CUT_BACK_MIN, Math.max(1, minutes)).apply();
    }

    public static int getUsageSeriouslyAddictedMinutes(Context context) {
        int cutBack = getUsageCutBackMinutes(context);
        int raw = prefs(context).getInt(KEY_USAGE_SERIOUSLY_ADDICTED_MIN, DEFAULT_USAGE_SERIOUSLY_ADDICTED_MIN);
        return Math.max(cutBack, raw);
    }

    public static void setUsageSeriouslyAddictedMinutes(Context context, int minutes) {
        int cutBack = getUsageCutBackMinutes(context);
        prefs(context).edit().putInt(KEY_USAGE_SERIOUSLY_ADDICTED_MIN, Math.max(cutBack, minutes)).apply();
    }
}
