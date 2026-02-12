package com.aalaa.foodplanner.datasource.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface PendingActionDao {
    @Insert
    Completable insert(PendingAction action);

    @Delete
    Completable delete(PendingAction action);

    @Query("SELECT * FROM pending_actions ORDER BY timestamp ASC")
    Single<List<PendingAction>> getAll();

    @Query("DELETE FROM pending_actions")
    Completable clearAll();
}
