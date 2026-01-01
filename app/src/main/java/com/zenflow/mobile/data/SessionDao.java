package com.zenflow.mobile.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface SessionDao {

    @Insert
    void insert(SessionEntity s);

    @Query("SELECT * FROM sessions ORDER BY startTs DESC")
    List<SessionEntity> getAll();
}
