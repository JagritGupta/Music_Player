package com.example.myplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyService extends Service {

    MediaPlayer myPlayer;
    int pos;
    int totalDuration;
    int currentPlayerPosition;
    IBinder mBinder;


    class MyServiceBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mBinder = new MyServiceBinder();
        Bundle bundle = intent.getExtras();
        pos = bundle.getInt("SongPos");
        myPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(Utility.songsList.get(pos).getPath()));
        myPlayer.start();
        return START_STICKY;
    }

    public int getTotalDuration() {
        totalDuration = myPlayer.getDuration();
        return totalDuration;
    }

    public int getCurrentPlayerPosition() {
        return myPlayer.getCurrentPosition();
    }
}
