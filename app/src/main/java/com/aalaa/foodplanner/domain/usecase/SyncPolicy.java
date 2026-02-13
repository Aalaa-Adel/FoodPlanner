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

    public boolean shouldRunRestore(String uid) {
        if (uid == null)
            return false;
        boolean isOnline = connectivityObserver.isConnected();
        boolean shouldRestore = sessionManager.shouldRestore(uid);
        return isOnline && shouldRestore;
    }

    public boolean shouldRunBackup() {
        return connectivityObserver.isConnected();
    }
}
