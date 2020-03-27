package com.example.myplayer;//Singleton Class

import android.os.Parcel;
import android.os.Parcelable;

public class SongDetails implements Parcelable {
    String songTitle,songDesc;
    int imageId,position;
    int songID;
    int currentPosition,totalDuration;

    private static SongDetails songDetails;

    private SongDetails(){    }

    protected SongDetails(Parcel in) {
        songTitle = in.readString();
        songDesc = in.readString();
        imageId = in.readInt();
        position = in.readInt();
        songID = in.readInt();
        currentPosition = in.readInt();
        totalDuration = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songTitle);
        dest.writeString(songDesc);
        dest.writeInt(imageId);
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


}
