package com.aalaa.foodplanner.data.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "pending_actions")
public class PendingAction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "action_type")
    public String actionType;

    @ColumnInfo(name = "entity_type")
    public String entityType;

    @ColumnInfo(name = "payload")
    public String payload;

    @ColumnInfo(name = "timestamp")
    public long timestamp;

    public PendingAction(String actionType, String entityType, String payload, long timestamp) {
        this.actionType = actionType;
        this.entityType = entityType;
        this.payload = payload;
        this.timestamp = timestamp;
    }
}
