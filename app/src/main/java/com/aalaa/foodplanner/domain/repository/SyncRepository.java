package com.aalaa.foodplanner.domain.repository;

import io.reactivex.rxjava3.core.Completable;

public interface SyncRepository {
    Completable backupUserData();

    Completable restoreUserData();

    Completable clearLocalData();

    Completable processPendingActions();
}
