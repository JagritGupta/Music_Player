package com.example.myplayer;

import android.content.Context;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.Room;

@Database(entities = {SongDetails.class}, version = 3)
public abstract class SongRoomDatabase extends RoomDatabase {
    public abstract DAO dao();

    private static volatile SongRoomDatabase roomDatabaseInstance;

    static SongRoomDatabase getDatabase(final Context context) {
        if (roomDatabaseInstance == null) {
            synchronized (SongRoomDatabase.class) {
                if (roomDatabaseInstance == null) {
                    roomDatabaseInstance = Room.databaseBuilder(context,
                            SongRoomDatabase.class, "songsList")
                            .build();
                }
            }
        }
        return roomDatabaseInstance;
        }

}
