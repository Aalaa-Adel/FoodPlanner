package com.aalaa.foodplanner.domain.usecase;

import com.aalaa.foodplanner.data.local.SessionManager;
import com.aalaa.foodplanner.data.network.ConnectivityObserver;

public class SyncPolicy {

    private final SessionManager sessionManager;
    private final ConnectivityObserver connectivityObserver;

    public SyncPolicy(SessionManager sessionManager, ConnectivityObserver connectivityObserver) {
        this.sessionManager = sessionManager;
        this.connectivityObserver = connectivityObserver;
    }

    /**
     * Determines if a data restore should be attempted.
     * 
     * @param uid The current user's UID.
     * @return true if the user is online AND has not restored data on this device
     *         yet.
     */
    public boolean shouldRunRestore(String uid) {
        if (uid == null)
            return false;
        boolean isOnline = connectivityObserver.isConnected();
        boolean shouldRestore = sessionManager.shouldRestore(uid);
        return isOnline && shouldRestore;
    }

    /**
     * Determines if a backup should be attempted.
     * This is a simple check for online status, but could be expanded.
     * 
     * @return true if online.
     */
    public boolean shouldRunBackup() {
        return connectivityObserver.isConnected();
    }
}
