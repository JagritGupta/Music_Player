package com.example.myplayer;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "songsDB")
public class SongDetails implements Parcelable {

    String songID;

    @ColumnInfo(name = "songName")
    String songTitle;

    @ColumnInfo(name = "songDesc")
    String songDesc;

    @ColumnInfo(name = "songAlbumArt")
    byte[] songAlbumArt;

    @ColumnInfo(name = "playerType")
    String playerType;          //Player type can be either Downloads or Festival bcz way of playing is diff for both

    @ColumnInfo(name = "typeOfPlaylist")
    String typeOfPlaylist="";  //Type of playlist can be anything created by the user

    @ColumnInfo(name = "position")
    int position = -1;    //position variable is used for playing Festival songs

    @PrimaryKey
    @NonNull
    String songPath;      //songPath variable is used for playing Download songs

    boolean isFavourite=false;

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

    public String getPlayerType() {
        return playerType;
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

    public String getTypeOfPlaylist() {
        return typeOfPlaylist;
    }

    @NonNull
    public String getSongPath() {
        return songPath;
    }

    public void setTypeOfPlaylist(String typeOfPlaylist) {
        this.typeOfPlaylist = typeOfPlaylist;
    }

    public void setSongPath(@NonNull String songPath) {
        this.songPath = songPath;
    }


    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }




    //PARCELABLE CODE

    protected SongDetails(Parcel in) {
        songID = in.readString();
        songTitle = in.readString();
        songDesc = in.readString();
        songAlbumArt = in.createByteArray();
        playerType = in.readString();
        position = in.readInt();
        songPath = in.readString();
        isFavourite = in.readByte() != 0;
    }


    public static final Creator<SongDetails> CREATOR = new Creator<SongDetails>() {
        @Override
        public SongDetails createFromParcel(Parcel in) {
            return new SongDetails(in);
        }

        @Override
        public SongDetails[] newArray(int size) {
            return new SongDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songID);
        dest.writeString(songTitle);
        dest.writeString(songDesc);
        dest.writeByteArray(songAlbumArt);
        dest.writeString(playerType);
        dest.writeInt(position);
        dest.writeString(songPath);
        dest.writeByte((byte) (isFavourite ? 1 : 0));
    }
}