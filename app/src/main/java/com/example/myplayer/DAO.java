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

    @Query("SELECT * FROM songsDB where isFavourite='1'")   //1 implies true in SQL
    List<SongDetails> getAllFavSongs();

    @Query("SELECT * FROM songsDB where playerType='Downloads'")
    List<SongDetails> getAllDownloads();

    @Query("SELECT * FROM songsDB where playerType='Festival'")
    List<SongDetails> getAllFestival();

    @Query("SELECT * FROM songsDB ORDER BY songName ASC")
    List<SongDetails> getAllSongs();

    @Query("UPDATE songsDB SET typeOfPlaylist=:playlistName where songPath=:songPaths")
    void setPlaylistName(String playlistName,String songPaths);

    @Query("SELECT DISTINCT typeOfPlaylist FROM songsDB ORDER BY typeOfPlaylist ASC")
    List<String> getAllPlaylists();

    @Query(value = "SELECT * FROM songsDB where typeOfPlaylist LIKE '%+' || :searchPlaylist || '+%'")
    List<SongDetails> getSongsFromPlaylist(String searchPlaylist);

    @Update
    void updateDB(SongDetails songDetails);

    @Delete
    int deleteSong(SongDetails songDetails);


}
