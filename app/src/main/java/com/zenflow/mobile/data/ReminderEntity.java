package com.zenflow.mobile.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders")
public class ReminderEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public long dateEpochDay;

    public long createdTs;

    public String text;
}
