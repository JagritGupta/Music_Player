package com.example.myplayer;//Singleton Class

import android.os.Parcel;
import android.os.Parcelable;


public class SongDetails implements Parcelable {

    String songTitle;
    String songDesc;
    byte[] songAlbumArt;
    int position = -1;
    int songImageID;
    public int songID = 0;
    String path = null;
    String playlistType;
    int totalDuration;


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

    protected SongDetails(Parcel in) {
        songTitle = in.readString();
        songDesc = in.readString();
        position = in.readInt();
        songID = in.readInt();
        totalDuration = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songTitle);
        dest.writeString(songDesc);
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

    public byte[] getSongAlbumArt() {
        return songAlbumArt;
    }

    public int getPosition() {
        return position;
    }

    public int getSongImageID() {
        return songImageID;
    }

    public int getSongID() {
        return songID;
    }

    public String getPath() {
        return path;
    }

    public String getPlaylistType() {
        return playlistType;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public static Creator<SongDetails> getCREATOR() {
        return CREATOR;
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

    public void setSongImageID(int songImageID) {
        this.songImageID = songImageID;
    }

    public void setSongID(int songID) {
        this.songID = songID;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPlaylistType(String playlistType) {
        this.playlistType = playlistType;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }
}