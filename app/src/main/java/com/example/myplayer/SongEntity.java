package com.example.myplayer;

import androidx.annotation.NonNull;
import androidx.annotation.PluralsRes;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "songsList")
public class SongEntity {
    /*@NonNull
    String path = null;*/

    @PrimaryKey
    @NonNull
    String id;

    @NonNull
    @ColumnInfo(name = "songName")
    String songTitle;

    @NonNull
    @ColumnInfo(name = "songDesc")
    String songDesc;  //Blob datatype for bytearray


    /*@NonNull
    @ColumnInfo(name = "songAlbumArt")
    byte[] songAlbumArt;

    @NonNull
    @ColumnInfo(name = "playlistType")
    String playlistType;

    @NonNull
    @ColumnInfo(name = "position")
    int position = -1;

    @NonNull
    @ColumnInfo(name = "songImageID")
    int songImageID;

    public int songID = 0;
    int totalDuration;*/

    public SongEntity(String uniqueID, SongDetails songDetails) {
        this.id = uniqueID;
        this.songTitle = songDetails.songTitle;
        this.songDesc = songDetails.songDesc;
    }

    public SongEntity() {
    }

    /* @NonNull
    public String getPath() {
        return path;
    }

    public void setPath(@NonNull String path) {
        this.path = path;
    }*/

    @NonNull
    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(@NonNull String songTitle) {
        this.songTitle = songTitle;
    }

    @NonNull
    public String getSongDesc() {
        return songDesc;
    }

    public void setSongDesc(@NonNull String songDesc) {
        this.songDesc = songDesc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /* @NonNull
    public byte[] getSongAlbumArt() {
        return songAlbumArt;
    }

    public void setSongAlbumArt(@NonNull byte[] songAlbumArt) {
        this.songAlbumArt = songAlbumArt;
    }

    @NonNull
    public String getPlaylistType() {
        return playlistType;
    }

    public void setPlaylistType(@NonNull String playlistType) {
        this.playlistType = playlistType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getSongImageID() {
        return songImageID;
    }

    public void setSongImageID(int songImageID) {
        this.songImageID = songImageID;
    }

    public int getSongID() {
        return songID;
    }

    public void setSongID(int songID) {
        this.songID = songID;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }*/
}

