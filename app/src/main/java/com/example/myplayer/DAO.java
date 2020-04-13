package com.example.myplayer;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface DAO {

    @Insert
    void insert(SongDetails songDetails);

    @Query("SELECT * FROM songsList")
    List<SongDetails> getAllFavSongs();

    @Delete
    int deleteSong(SongDetails songDetails);


}
