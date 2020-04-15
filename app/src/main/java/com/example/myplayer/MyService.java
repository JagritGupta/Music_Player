package com.example.myplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.myplayer.Notification.OnClearFromRecentServiceSearch;

import java.util.ArrayList;

public class MyService extends Service {

    MediaPlayer mediaPlayer = null;
    int recyclerViewPosition;
    int totalDuration;
    int currentPosition;
    String actionType;
    NotificationManager notificationManager;
    private static MyService instance;
    IBinder mBinder;
    SongDetails songDetails;
    ArrayList<SongDetails> songsList;
    private final int[] festivalSongsList = Utility.getFestivalList();
    private boolean isPlaying;

    @Override
    public void onCreate() {
        super.onCreate();

        songsList = Utility.getSongsList();
        //Create notification

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("SONGS_LIST"));
            startService(new Intent(getBaseContext(), OnClearFromRecentServiceSearch.class));
        }
    }



    class MyServiceBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    public static MyService getInstance() {
        if(instance == null) {
            instance = new MyService();
        }
        return instance;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


   /* @Override
    public void onDestroy() {
        super.onDestroy();
    }*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mBinder = new MyServiceBinder();
        Bundle bundle = intent.getExtras();
        recyclerViewPosition = bundle.getInt("SongPos");
        songDetails = songsList.get(recyclerViewPosition);
        playMedia();
        CreateNotification.createNotification(MyService.this, songDetails, isPlaying, 1, songsList.size() - 1);

        /*actionType=bundle.getString("ActionType");

        switch (actionType){

            case ACTION_PLAY:
                playMedia();
            case ACTION_PAUSE:
                pausePlaying();

        }*/
        return START_STICKY;
    }

    public void playMedia() {
        if (mediaPlayer != null)
            mediaPlayer = null;

        if (songDetails.playlistType.equalsIgnoreCase("Festival"))                  //type FESTIVAL
            mediaPlayer = MediaPlayer.create(getApplicationContext(), festivalSongsList[songDetails.getPosition()]);

        else {            //type DOWNLOADS
            Uri uri = Uri.parse(songDetails.getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        }

        mediaPlayer.start();
        isPlaying = true;
    }

    public void pausePlaying() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            CreateNotification.createNotification(MyService.this, songDetails,
                    isPlaying, recyclerViewPosition, songsList.size() - 1);

        } else {
            isPlaying = true;
            CreateNotification.createNotification(MyService.this, songDetails,
                    isPlaying, recyclerViewPosition, songsList.size() - 1);
            mediaPlayer.start();
        }
    }

    public void playNextSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            recyclerViewPosition = (recyclerViewPosition + 1) % songsList.size();
            songDetails = songsList.get(recyclerViewPosition);
            playMedia();
            CreateNotification.createNotification(MyService.this, songDetails,
                    isPlaying, recyclerViewPosition, songsList.size() - 1);
        }
    }

    public void playPreviousSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            recyclerViewPosition = (recyclerViewPosition - 1) % songsList.size();
            if (recyclerViewPosition < 0)
                recyclerViewPosition = songsList.size() - 1;
            songDetails = songsList.get(recyclerViewPosition);
            playMedia();
            CreateNotification.createNotification(MyService.this, songDetails,
                    isPlaying, recyclerViewPosition, songsList.size() - 1);
        }
    }

    public void next10() {
        currentPosition = mediaPlayer.getCurrentPosition() + 10000;

        if (currentPosition >= totalDuration)
            currentPosition = totalDuration;

        mediaPlayer.seekTo(currentPosition);
    }

    public void prev10sec(){
        currentPosition = mediaPlayer.getCurrentPosition() - 10000;

        if (currentPosition <= 0)
            currentPosition = 0;

        mediaPlayer.seekTo(currentPosition);
    }

    public int getTotalDuration() {
        totalDuration = mediaPlayer.getDuration();
        return totalDuration;
    }

    public int getCurrentPlayerPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public void changeSeekBarPosition(int pos){
        mediaPlayer.seekTo(pos);

    }
    public boolean isMediaPlaying() {
        if (mediaPlayer.isPlaying()) {
            return true;
        } else {
            return false;
        }
    }

    public SongDetails getCurrentSongObject() {
        return songDetails;
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID, "JG", NotificationManager.IMPORTANCE_LOW);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager = getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }

        }
    }


    //Below func are for Notifcations usage

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action) {

                case CreateNotification.ACTION_PLAY:
                    pausePlaying();
                    break;

                case CreateNotification.ACTION_NEXT:
                    playNextSong();
                    break;

                case CreateNotification.ACTION_PREVIOUS:
                    playPreviousSong();
                    break;
            }
        }
    };



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
    }
}
