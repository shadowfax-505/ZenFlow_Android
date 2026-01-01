package com.zenflow.mobile.auth;

import android.content.Context;

import com.zenflow.mobile.data.AppDatabase;

public final class SessionManager {

    private SessionManager() {}

    public static boolean isLoggedIn(Context c) {
        return AppDatabase.isLoggedIn(c);
    }

    public static String getLoggedInUser(Context c) {
        return c.getApplicationContext()
                .getSharedPreferences("zenflow_auth", Context.MODE_PRIVATE)
                .getString("logged_in_user", null);
    }

    public static void login(Context c, String username) {
        AppDatabase.setLoggedInUser(c, username);
    }

    public static void logout(Context c) {
        AppDatabase.clearSession(c);
    }
}
