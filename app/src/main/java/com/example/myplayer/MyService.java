package com.example.myplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyService extends Service {
    MediaPlayer myPlayer;
    String path;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle=intent.getExtras();
        path= (String) bundle.get("mediaPlayer");
        myPlayer=MediaPlayer.create(getApplicationContext(),Uri.parse(path));
        myPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }
}
