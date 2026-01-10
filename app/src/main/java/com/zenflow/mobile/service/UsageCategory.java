package com.zenflow.mobile.service;

public enum UsageCategory {
    OK("OK"),
    CUT_BACK("Need to cut back"),
    SERIOUSLY_ADDICTED("Seriously addicted");

    public final String label;

    UsageCategory(String label) {
        this.label = label;
    }
}

