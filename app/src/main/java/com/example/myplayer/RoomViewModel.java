package com.example.myplayer;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class RoomViewModel extends AndroidViewModel {

    private DAO dao;
    private SongRoomDatabase roomDB;
    private LiveData<List<SongEntity>> allFavSongs;

    public RoomViewModel(@NonNull Application application) {
        super(application);
        roomDB = SongRoomDatabase.getDatabase(application);
        dao = roomDB.dao();
        allFavSongs=dao.getAllFavSongs();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void insert(SongEntity songEntity) {
        new InsertAsyncTask(dao).execute(songEntity);
    }

    LiveData<List<SongEntity>> fetchAllFavSongs(){
        return allFavSongs;
    }

    private class InsertAsyncTask extends AsyncTask<SongEntity, Void, Void> {

        DAO myDao;

        public InsertAsyncTask(DAO dao) {
            this.myDao = dao;
        }

        @Override
        protected Void doInBackground(SongEntity... songEntities) {
            myDao.insert(songEntities[0]);
            return null;
        }
    }
}
