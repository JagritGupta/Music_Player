package com.example.myplayer;

public class MusicLibrary {
    String songTitle,songDesc;
    int songImage;

    public MusicLibrary(){}

    public MusicLibrary(String songTitle, String songDesc, int songImage) {
        this.songTitle = songTitle;
        this.songDesc = songDesc;
        this.songImage = songImage;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongDesc() {
        return songDesc;
    }

    public void setSongDesc(String songDesc) {
        this.songDesc = songDesc;
    }

    public int getSongImage() {
        return songImage;
    }

    public void setSongImage(int songImage) {
        this.songImage = songImage;
    }
}
