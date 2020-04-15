package com.example.myplayer;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface DAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SongDetails songDetails);

    @Query("SELECT * FROM songsDB where isFavourite = 'True' ")
    List<SongDetails> getAllFavSongs();

    @Query("SELECT * FROM songsDB where playlistType='Downloads' ")
    List<SongDetails> getAllDownloads();

    @Query("SELECT * FROM songsDB where playlistType='Festival' ")
    List<SongDetails> getAllFestival();

    @Query("SELECT * FROM songsDB")
    List<SongDetails> getAllSongs();

    @Delete
    int deleteSong(SongDetails songDetails);


}
