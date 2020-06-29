package com.example.myplayer;

import android.app.Application;
import android.os.AsyncTask;


import java.util.List;
import java.util.concurrent.ExecutionException;

public class RoomService {
    private SongRoomDatabase roomDB;

    public RoomService(Application context) {
        roomDB= SongRoomDatabase.getDatabase(context);
    }

    public void insert(SongDetails songDetails) {
        new InsertAsyncTask().execute(songDetails);
    }

    public void updateDB(SongDetails songDetails) {
        new UpdateAsyncTask().execute(songDetails);
    }

    public void deleteSong(SongDetails songDetails) {
        new DeleteAsyncTask().execute(songDetails);
    }

    public List<SongDetails> fetchFestivals() {
        try {
            return new DisplayFestivalAsyncTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<SongDetails> fetchFavourites() {
        try {
            return new DisplayFavouriteAsyncTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAllPlaylists(){
        try {
            return new GetPlaylistsAsyncTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }



    public List<SongDetails> fetchDownloads() {
        try {
            return new DisplayDownloadAsyncTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<SongDetails> fetchAllSongs() {
        try {
            return new DisplayAllSongsAsyncTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPlaylistName(String playlistName,String songPath) {
        try {
            new SetPlaylistNameAsyncTask(playlistName,songPath).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<SongDetails> getSongsFromPlaylist(String playlistName) {
        try {
            return new GetSongsFromPlaylists(playlistName).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private class InsertAsyncTask extends AsyncTask<SongDetails, Void, Void> {
        public InsertAsyncTask() {
        }

        @Override
        protected Void doInBackground(SongDetails... songsList) {
            roomDB.dao().insert(songsList[0]);
            return null;
        }
    }

    private class UpdateAsyncTask extends AsyncTask<SongDetails, Void, Void> {
        public UpdateAsyncTask() {
        }

        @Override
        protected Void doInBackground(SongDetails... songsList) {
            roomDB.dao().updateDB(songsList[0]);
            return null;
        }
    }

    private class SetPlaylistNameAsyncTask<playlistName> extends AsyncTask<Object,Void,Void>{

        String playlistName;
        String songPath;

        public SetPlaylistNameAsyncTask(String playlistName, String songPath) {
            this.playlistName = playlistName;
            this.songPath = songPath;
        }

        @Override
        protected Void doInBackground(Object... objects) {
            roomDB.dao().setPlaylistName(playlistName,songPath);
            return null;
        }
    }

    private class GetPlaylistsAsyncTask extends AsyncTask<Void,Void,List<String>>{

        @Override
        protected List<String> doInBackground(Void... voids) {
            return roomDB.dao().getAllPlaylists();
        }
    }

    private class DisplayFestivalAsyncTask extends AsyncTask<Void, Void, List<SongDetails>> {
        @Override
        protected List<SongDetails> doInBackground(Void... voids) {
            return roomDB.dao().getAllFestival();
        }
    }

    private class DisplayFavouriteAsyncTask extends AsyncTask<Void, Void, List<SongDetails>> {
        @Override
        protected List<SongDetails> doInBackground(Void... voids) {
            return roomDB.dao().getAllFavSongs();
        }
    }

    private class DisplayDownloadAsyncTask extends AsyncTask<Void, Void, List<SongDetails>> {
        @Override
        protected List<SongDetails> doInBackground(Void... voids) {
            return roomDB.dao().getAllDownloads();
        }
    }

    private class DisplayAllSongsAsyncTask extends AsyncTask<Void, Void, List<SongDetails>> {
        @Override
        protected List<SongDetails> doInBackground(Void... voids) {
            return roomDB.dao().getAllSongs();
        }
    }

    private class GetSongsFromPlaylists extends AsyncTask<String, Void, List<SongDetails>> {

        String playlistName;

        public GetSongsFromPlaylists(String playlistName) {
            this.playlistName = playlistName;
        }

        @Override
        protected List<SongDetails> doInBackground(String... strings) {
            return roomDB.dao().getSongsFromPlaylist(playlistName);
        }
    }

    private class DeleteAsyncTask extends AsyncTask<SongDetails, Void, Void> {
        @Override
        protected Void doInBackground(SongDetails... songsList) {
            roomDB.dao().deleteSong(songsList[0]);
            return null;
        }
    }
}
