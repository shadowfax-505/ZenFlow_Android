package com.zenflow.mobile.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SessionEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase get(Context c) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    c.getApplicationContext(),
                    AppDatabase.class,
                    "zenflow.db"
            ).build();
        }
        return INSTANCE;
    }

    public abstract SessionDao sessionDao();
}
