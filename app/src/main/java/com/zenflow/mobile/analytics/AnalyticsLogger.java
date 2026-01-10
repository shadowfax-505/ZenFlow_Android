package com.zenflow.mobile.analytics;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;

public final class AnalyticsLogger {

    private AnalyticsLogger() {}

    public static void logScreenView(Context context, String screenName, String screenClass) {
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass);
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params);
    }

    public static void logLogin(Context context, String method, @Nullable Integer usernameLength) {
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.METHOD, method);
        if (usernameLength != null) {
            params.putInt("username_length", usernameLength);
        }
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN, params);
    }

    public static void logAdminLogin(Context context, boolean success) {
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);
        Bundle params = new Bundle();
        params.putString("success", success ? "1" : "0");
        analytics.logEvent("admin_login", params);
    }

    public static void logSettingsChanged(Context context, String key) {
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);
        Bundle params = new Bundle();
        params.putString("key", key);
        analytics.logEvent("settings_changed", params);
    }
}

