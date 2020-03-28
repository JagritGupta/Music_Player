package com.example.myplayer;//Singleton Class

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class SongDetails implements Parcelable {
    String songTitle,songDesc;
    int songImageID,position;
    private static ArrayList<SongDetails> songsList;
    public static int songID;
    int currentPosition,totalDuration;

    public static ArrayList<SongDetails> getSongsList() {
        return songsList;
    }


    public SongDetails(ArrayList<SongDetails> songsList){
        this.songsList=songsList;
    }

    public SongDetails(String songTitle, String songDesc, int songImageID) {
        this.songTitle = songTitle;
        this.songDesc = songDesc;
        this.songImageID = songImageID;
    }

    private SongDetails(){    }

    protected SongDetails(Parcel in) {
        songTitle = in.readString();
        songDesc = in.readString();
        songImageID = in.readInt();
        position = in.readInt();
        songID = in.readInt();
        currentPosition = in.readInt();
        totalDuration = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songTitle);
        dest.writeString(songDesc);
        dest.writeInt(songImageID);
        dest.writeInt(position);
        dest.writeInt(songID);
        dest.writeInt(currentPosition);
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

    public static SongDetails getInstance(){
        if(songDetails==null)
            songDetails=new SongDetails();
        return songDetails;
    }


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

    public int getCurrentPosition() {
        return currentPosition;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    private static SongDetails songDetails;


}
