package com.example.myplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Binder;

import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import com.example.myplayer.SearchOption.OnClearFromRecentServiceSearch;

import java.util.ArrayList;

public class MyService extends Service {

    static MediaPlayer mediaPlayer = null;
    static int recyclerViewPosition;
    int totalDuration;
    int currentPosition = 0;
    String actionType;
    NotificationManager notificationManager;
    private static MyService instance;
    IBinder mBinder;
    static SongDetails songDetails = null;
    ArrayList<SongDetails> songsList;
    private final int[] festivalSongsList = Utility.getFestivalList();
    private boolean isPlaying;
    public static final int FLAG_HANDLES_MEDIA_BUTTONS = 0;
    private MediaSession mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Calling", "MyService Created");

        songsList = Utility.getSongsList();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();

            //startService(new Intent(getBaseContext(), OnClearFromRecentServiceSearch.class));
        }
    }


    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }


    class MyServiceBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    public static MyService getInstance() {
        if (instance == null) {
            instance = new MyService();
        }
        return instance;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


   /*@Override
    public void onDestroy() {
        super.onDestroy();
    }*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mBinder = new MyServiceBinder();
        Bundle bundle = intent.getExtras();
        recyclerViewPosition = bundle.getInt("SongPos");
        /*songDetails = songsList.get(recyclerViewPosition);
        playMedia(songDetails);*/
        registerReceiver(broadcastReceiver,new IntentFilter("SONG_LIST"));
        return START_STICKY;
    }

    public void playMedia(int recyclerViewPosition) {
        fetchSongsList();
        songDetails = songsList.get(recyclerViewPosition);

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            //mediaPlayer = null;
        }


        if (songDetails.playlistType.equalsIgnoreCase("Festival"))                  //type FESTIVAL
            mediaPlayer = MediaPlayer.create(getApplicationContext(), festivalSongsList[songDetails.getPosition()]);

        else {            //type DOWNLOADS
            Uri uri = Uri.parse(songDetails.getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        }

        mediaPlayer.start();
        isPlaying = true;
        CreateNotification.createNotification(MyService.this, songDetails, isPlaying);

    }

    private void fetchSongsList() {
        songsList = Utility.getSongsList();
    }

    public void pausePlaying() {
        Log.e("Calling", "I PAUSE PLAY");

        if(MainActivity.isMainActivityVisible && mediaPlayer.isPlaying())
            MainActivity.miniPlayPauseBtn.setImageResource(R.drawable.play_btn);

        else if(MainActivity.isMainActivityVisible && !mediaPlayer.isPlaying())
            MainActivity.miniPlayPauseBtn.setImageResource(R.drawable.pause_btn);


        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;


        } else {
            isPlaying = true;
            mediaPlayer.start();
        }

        CreateNotification.createNotification(MyService.this, songDetails, isPlaying);
    }

    public void playNextSong() {

        Log.e("Calling", "I NEXT CALLED");
        recyclerViewPosition = (recyclerViewPosition + 1) % songsList.size();
        songDetails = songsList.get(recyclerViewPosition);
        playMedia(recyclerViewPosition);
        if (MainActivity.isMainActivityVisible)
            MainActivity.miniPlayerAccess();

    }

    public void playPreviousSong() {
        Log.e("Calling", "I PREVIOUS CALLED");
        recyclerViewPosition = (recyclerViewPosition - 1) % songsList.size();
        if (recyclerViewPosition < 0)
            recyclerViewPosition = songsList.size() - 1;
        songDetails = songsList.get(recyclerViewPosition);
        playMedia(recyclerViewPosition);

        if (MainActivity.isMainActivityVisible)
            MainActivity.miniPlayerAccess();
    }

    public void next10() {
        currentPosition = mediaPlayer.getCurrentPosition() + 10000;

        if (currentPosition >= totalDuration)
            currentPosition = totalDuration;

        mediaPlayer.seekTo(currentPosition);
    }

    public void prev10sec() {
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

    public void changeSeekBarPosition(int pos) {
        mediaPlayer.seekTo(pos);

    }

    public boolean isMediaPlaying() {
        if (mediaPlayer.isPlaying()) {
            return true;
        } else {
            return false;
        }
    }

    public int getRecyclerViewPosition() {
        return recyclerViewPosition;
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
                    PlayerActivity.pauseBtn.performClick();
                    break;

                case CreateNotification.ACTION_NEXT:
                    PlayerActivity.nextBtn.performClick();
                    break;

                case CreateNotification.ACTION_PREVIOUS:
                    PlayerActivity.prevBtn.performClick();
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
