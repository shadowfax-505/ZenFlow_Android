package com.zenflow.mobile.service;

public final class UsageCategoryPolicy {

    public final int cutBackMinutes;
    public final int seriouslyAddictedMinutes;

    public UsageCategoryPolicy(int cutBackMinutes, int seriouslyAddictedMinutes) {
        this.cutBackMinutes = Math.max(1, cutBackMinutes);
        this.seriouslyAddictedMinutes = Math.max(this.cutBackMinutes, seriouslyAddictedMinutes);
    }

    public UsageCategory categorize(long foregroundMs) {
        long mins = foregroundMs / 60000L;
        if (mins >= seriouslyAddictedMinutes) return UsageCategory.SERIOUSLY_ADDICTED;
        if (mins >= cutBackMinutes) return UsageCategory.CUT_BACK;
        return UsageCategory.OK;
    }
}

