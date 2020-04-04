package com.example.myplayer;//Singleton Class

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class SongDetails implements Parcelable {
    String songTitle,songDesc;

    public String getSongAlbumArt() {
        return songAlbumArt;
    }

    public void setSongAlbumArt(String songAlbumArt) {
        this.songAlbumArt = songAlbumArt;
    }

    String songAlbumArt;
    int songImageID;
    int position=-1;
    private static ArrayList<SongDetails> songsList;
    public int songID=0;
    String path=null,playlistType;
    int totalDuration;

    public static ArrayList<SongDetails> getSongsList() {
        return songsList;
    }


    public SongDetails(ArrayList<SongDetails> songsList){
        this.songsList=songsList;
    }

    public SongDetails(String songTitle, String songDesc, int songImageID,String songAlbumArt) {
        this.songTitle = songTitle;
        this.songDesc = songDesc;
        this.songImageID = songImageID;
        this.songAlbumArt=songAlbumArt;
    }

    public SongDetails(){    }

    protected SongDetails(Parcel in) {
        songTitle = in.readString();
        songDesc = in.readString();
        songImageID = in.readInt();
        position = in.readInt();
        songID = in.readInt();
        totalDuration = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songTitle);
        dest.writeString(songDesc);
        dest.writeInt(songImageID);
        dest.writeInt(position);
        dest.writeInt(songID);
        dest.writeInt(totalDuration);
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

    public String getSongTitle() {
        return songTitle;
    }

    public String getSongDesc() {
        return songDesc;
    }

    public int getSongImageID() {
        return songImageID;
    }

    public int getPosition() {
        return position;
    }

    public int getSongID() {
        return songID;
    }



    public int getTotalDuration() {
        return totalDuration;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public void setSongDesc(String songDesc) {
        this.songDesc = songDesc;
    }

    public void setSongImageID(int songImageID) {
        this.songImageID = songImageID;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public static void setSongsList(ArrayList<SongDetails> songsList) {
        SongDetails.songsList = songsList;
    }

    public void setSongID(int songID) {
        this.songID = songID;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getPlaylistType() {
        return playlistType;
    }

    public void setPlaylistType(String playlistType) {
        this.playlistType = playlistType;
    }

}
