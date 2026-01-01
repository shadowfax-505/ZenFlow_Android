package com.zenflow.mobile.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SessionEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    private static final String AUTH_PREF = "zenflow_auth";
    private static final String KEY_LOGGED_IN_USER = "logged_in_user";
    private static final String KEY_USERPASS_PREFIX = "userpass_";

    private static final String KEY_ADMIN_USER = "admin_user";
    private static final String KEY_ADMIN_PASS = "admin_pass";

    private static SharedPreferences authSp(Context c) {
        return c.getApplicationContext().getSharedPreferences(AUTH_PREF, Context.MODE_PRIVATE);
    }

    public static void ensureDefaultAdmin(Context c) {
        SharedPreferences sp = authSp(c);
        String existingU = sp.getString(KEY_ADMIN_USER, null);
        String existingP = sp.getString(KEY_ADMIN_PASS, null);

        boolean missing = existingU == null || existingP == null;
        boolean isOldDefault = "admin".equals(existingU) && "admin".equals(existingP);

        if (missing || isOldDefault) {
            sp.edit()
                    .putString(KEY_ADMIN_USER, "rahman")
                    .putString(KEY_ADMIN_PASS, "1234")
                    .apply();
        }
    }

    public static boolean validateAdmin(Context c, String username, String password) {
        if (username == null || password == null) return false;
        ensureDefaultAdmin(c);
        SharedPreferences sp = authSp(c);
        String u = sp.getString(KEY_ADMIN_USER, "rahman");
        String p = sp.getString(KEY_ADMIN_PASS, "1234");
        return username.equals(u) && password.equals(p);
    }

    public static boolean isLoggedIn(Context c) {
        return authSp(c).getString(KEY_LOGGED_IN_USER, null) != null;
    }

    public static void setLoggedInUser(Context c, String username) {
        authSp(c).edit().putString(KEY_LOGGED_IN_USER, username).apply();
    }

    public static void clearSession(Context c) {
        authSp(c).edit().remove(KEY_LOGGED_IN_USER).apply();
    }

    public static boolean userExists(Context c, String username) {
        return authSp(c).contains(KEY_USERPASS_PREFIX + username);
    }

    public static boolean registerUser(Context c, String username, String password) {
        if (username == null || username.trim().isEmpty()) return false;
        if (password == null || password.isEmpty()) return false;
        if (userExists(c, username)) return false;
        authSp(c).edit().putString(KEY_USERPASS_PREFIX + username, password).apply();
        return true;
    }

    public static boolean validateUser(Context c, String username, String password) {
        if (username == null || password == null) return false;
        String stored = authSp(c).getString(KEY_USERPASS_PREFIX + username, null);
        return stored != null && stored.equals(password);
    }

    public static AppDatabase get(Context c) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    c.getApplicationContext(),
                    AppDatabase.class,
                    "zenflow.db"
            ).build();
        }
        return INSTANCE;
    }

    public abstract SessionDao sessionDao();
}
