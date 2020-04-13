package com.example.myplayer;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.room.Room;


import java.util.ArrayList;
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

    public void deleteSong(SongDetails songDetails) {
        new DeleteAsyncTask().execute(songDetails);
    }

    public List<SongDetails> fetchAllFavSongs() {
        try {
            return new DisplayAsyncTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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

    private class DisplayAsyncTask extends AsyncTask<Void, Void, List<SongDetails>> {
        @Override
        protected List<SongDetails> doInBackground(Void... voids) {
            return roomDB.dao().getAllFavSongs();
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
