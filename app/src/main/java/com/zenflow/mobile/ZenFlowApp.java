package com.zenflow.mobile;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

public class ZenFlowApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            FirebaseApp.initializeApp(this);
        } catch (Exception e) {
            Log.e("ZenFlowApp", "Firebase init failed", e);
        }

        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        analytics.setAnalyticsCollectionEnabled(true);

        FirebaseApp app = FirebaseApp.getInstance();
        FirebaseOptions options = app.getOptions();
        Log.d("ZenFlowApp", "FirebaseApp initialized. projectId=" + options.getProjectId() + ", applicationId=" + options.getApplicationId());
    }
}

