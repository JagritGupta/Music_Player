package com.example.myplayer;

import androidx.annotation.NonNull;
import androidx.annotation.PluralsRes;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "songsList")
public class SongEntity {             //use of this class is same as SongDetails... but this is used for RoomDatabse

    @PrimaryKey
    @NonNull
    String songID;

    @NonNull
    @ColumnInfo(name = "songName")
    private String songTitle;

    @NonNull
    @ColumnInfo(name = "songDesc")
    private String songDesc;

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

    @NonNull
    @ColumnInfo(name = "songImageID")
    int songImageID;

    public SongEntity(SongDetails songDetails) {
        this.songTitle = songDetails.songTitle;
        this.songDesc = songDetails.songDesc;
        this.songAlbumArt = songDetails.songAlbumArt;
        this.playlistType = songDetails.playlistType;
        this.position = songDetails.position;
        this.songID = songDetails.songID;
        this.isFavourite=songDetails.isFavourite;
        this.songPath = songDetails.path;
    }


    public SongEntity() {
    }

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

    @NonNull
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

    public String getSongID() {
        return songID;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }
}

