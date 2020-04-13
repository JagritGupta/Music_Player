package com.example.myplayer;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "songsList")
public class SongDetails {

    @PrimaryKey
    @NonNull
    String songID;

    @NonNull
    @ColumnInfo(name = "songName")
    String songTitle;

    @NonNull
    @ColumnInfo(name = "songDesc")
    String songDesc;

    @NonNull
    @ColumnInfo(name = "songAlbumArt")
    byte[] songAlbumArt;

    @NonNull
    @ColumnInfo(name = "playlistType")
    String playlistType;

    @NonNull
    @ColumnInfo(name = "position")
    int position = -1;    //position variable is used for playing Festival songs

    String songPath;      //songPath variable is used for playing Download songs

    boolean isFavourite;

    public SongDetails(String songTitle, String songDesc, byte[] songAlbumArt) {
        this.songTitle = songTitle;
        this.songDesc = songDesc;
        this.songAlbumArt = songAlbumArt;
    }


    public SongDetails(String songTitle, String songDesc) {
        this.songTitle = songTitle;
        this.songDesc = songDesc;
    }

    public SongDetails() {
    }


    public String getSongTitle() {
        return songTitle;
    }

    public String getSongDesc() {
        return songDesc;
    }

    public byte[] getSongAlbumArt() {
        return songAlbumArt;
    }

    public int getPosition() {
        return position;
    }

    public String getSongID() {
        return songID;
    }

    public String getPlaylistType() {
        return playlistType;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public String getPath() {
        return songPath;
    }

    public void setPath(String songPath) {
        this.songPath = songPath;
    }

    public void setIsFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public void setSongDesc(String songDesc) {
        this.songDesc = songDesc;
    }

    public void setSongAlbumArt(byte[] songAlbumArt) {
        this.songAlbumArt = songAlbumArt;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }


    public void setPlaylistType(String playlistType) {
        this.playlistType = playlistType;
    }
}