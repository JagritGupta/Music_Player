package com.example.myplayer;

import java.io.File;
import java.util.ArrayList;

public class Utility {
    public static ArrayList<SongDetails> songsList;
    public static ArrayList<File> downloadsList;
    public static int[] festivalList;

    public static ArrayList<SongDetails> getSongsList() {
        return songsList;
    }

    public static void setSongsList(ArrayList<SongDetails> songsList) {
        Utility.songsList = songsList;
    }

    public static ArrayList<File> getDownloadsList() {
        return downloadsList;
    }

    public static void setDownloadsList(ArrayList<File> downloadsList) {
        Utility.downloadsList = downloadsList;
    }

    public static int[] getFestivalList() {
        return festivalList;
    }

    public static void setFestivalList(int[] festivalList) {
        Utility.festivalList = festivalList;
    }
}
