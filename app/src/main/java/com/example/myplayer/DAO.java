package com.example.myplayer;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DAO {

    @Insert
    void insert(SongEntity songEntity);

    @Query("SELECT * FROM songsList")
    LiveData<List<SongEntity>> getAllFavSongs();

    @Delete
    int deleteSong(SongEntity songEntity);


}
