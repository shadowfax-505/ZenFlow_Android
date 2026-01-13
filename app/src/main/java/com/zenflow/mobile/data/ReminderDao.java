package com.zenflow.mobile.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReminderDao {

    @Insert
    void insert(ReminderEntity r);

    @Delete
    void delete(ReminderEntity r);

    @Query("SELECT * FROM reminders WHERE dateEpochDay = :epochDay ORDER BY createdTs DESC")
    List<ReminderEntity> getByEpochDay(long epochDay);

    @Query("SELECT * FROM reminders WHERE dateEpochDay BETWEEN :startEpochDay AND :endEpochDay ORDER BY dateEpochDay ASC, createdTs ASC")
    List<ReminderEntity> getUpcoming(long startEpochDay, long endEpochDay);
}

