package com.aalaa.foodplanner.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "food_planner_session";
    private static final String KEY_IS_GUEST = "is_guest";
    private static final String KEY_PREFIX_DID_RESTORE = "did_restore_";

    private final SharedPreferences prefs;

    private static SessionManager instance;

    private SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public boolean isGuest() {
        return prefs.getBoolean(KEY_IS_GUEST, false);
    }

    public void setGuest(boolean isGuest) {
        prefs.edit().putBoolean(KEY_IS_GUEST, isGuest).apply();
    }


    public boolean shouldRestore(String uid) {
        if (uid == null)
            return false;

        return !prefs.getBoolean(KEY_PREFIX_DID_RESTORE + uid, false);
    }


    public void setRestored(String uid) {
        if (uid == null)
            return;
        prefs.edit().putBoolean(KEY_PREFIX_DID_RESTORE + uid, true).apply();
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }


    public void clearGuest() {
        prefs.edit().remove(KEY_IS_GUEST).apply();
    }
}
