package com.example.myplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static List<String> getAllPlaylists(RoomService roomService) {
        List<String> playlistNames=roomService.getAllPlaylists();
        playlistNames.clear();
        List<String> tempList;
        List<String> dbList=roomService.getAllPlaylists();
        for(int i=0;i<dbList.size();i++){
            tempList= Arrays.asList(dbList.get(i).split("[+]"));
            for(int j=0;j<tempList.size();j++){
                String elt=tempList.get(j);
                if(elt.length()>0){
                    if(!playlistNames.contains(elt)){
                        playlistNames.add(elt);
                    }
                }
            }
        }
        return playlistNames;
    }

    public static int[] getFestivalList() {
        return festivalList;
    }

    public static void setFestivalList(int[] festivalList) {
        Utility.festivalList = festivalList;
    }
}
