package com.zenflow.mobile.service;

public class AppUsageItem {
    public final String packageName;
    public final String appLabel;
    public final long timeForegroundMs;

    public AppUsageItem(String packageName, String appLabel, long timeForegroundMs) {
        this.packageName = packageName;
        this.appLabel = appLabel;
        this.timeForegroundMs = timeForegroundMs;
    }
}

