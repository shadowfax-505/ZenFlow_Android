package com.zenflow.mobile.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sessions")
public class SessionEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public long startTs;
    public Long endTs;
    public String type;
    public boolean completed;
}
