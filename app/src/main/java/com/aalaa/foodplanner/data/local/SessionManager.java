package com.aalaa.foodplanner.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "food_planner_session";
    private static final String KEY_IS_GUEST = "is_guest";
    private static final String KEY_PREFIX_DID_RESTORE = "did_restore_";

    private final SharedPreferences prefs;

    // Singleton instance
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

    /**
     * Checks if the user is currently in Guest mode.
     */
    public boolean isGuest() {
        return prefs.getBoolean(KEY_IS_GUEST, false);
    }

    /**
     * Sets the Guest mode status.
     */
    public void setGuest(boolean isGuest) {
        prefs.edit().putBoolean(KEY_IS_GUEST, isGuest).apply();
    }

    /**
     * Checks if data restore has already been performed for a specific user UID.
     * 
     * @param uid The Firebase User UID.
     * @return true if restore has been completed, false otherwise.
     */
    public boolean shouldRestore(String uid) {
        if (uid == null)
            return false;
        // If "did_restore" is FALSE, it means we SHOULD restore.
        // So return !didRestore
        return !prefs.getBoolean(KEY_PREFIX_DID_RESTORE + uid, false);
    }

    /**
     * Marks a specific user UID as having completed the restore process.
     * 
     * @param uid The Firebase User UID.
     */
    public void setRestored(String uid) {
        if (uid == null)
            return;
        prefs.edit().putBoolean(KEY_PREFIX_DID_RESTORE + uid, true).apply();
    }

    /**
     * Clears all session data. useful for full app reset or debug.
     * careful: this might reset restore flags for users.
     */
    public void clearAll() {
        prefs.edit().clear().apply();
    }

    /**
     * Clears guest flag only.
     */
    public void clearGuest() {
        prefs.edit().remove(KEY_IS_GUEST).apply();
    }
}
