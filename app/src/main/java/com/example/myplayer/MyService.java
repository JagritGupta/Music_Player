package com.example.myplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myplayer.SearchOption.NotificationActionService;
import com.example.myplayer.SearchOption.OnClearFromRecentServiceSearch;

import java.util.ArrayList;

public class MyService extends Service {

    static MediaPlayer mediaPlayer = null;
    static int recyclerViewPosition;
    int totalDuration;
    int currentPosition = 0;
    NotificationManager notificationManager;
    private static MyService instance;
    IBinder mBinder;
    static SongDetails songDetails = null;
    ArrayList<SongDetails> songsList;
    private final int[] festivalSongsList = Utility.getFestivalList();
    private boolean isPlaying;
    public static final String CHANNEL_ID = "channel1";
    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static Notification notification;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Calling", "MyService Created");

        songsList = Utility.getSongsList();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }

        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    if(mediaPlayer!=null && mediaPlayer.isPlaying()){
                        pausePlaying();
                    }
                }
                else if(state == TelephonyManager.CALL_STATE_IDLE) {
                    pausePlaying();
                }
                else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                }

                super.onCallStateChanged(state, incomingNumber);
            }
        };
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }    }


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
        createNotification(MyService.this, songDetails, isPlaying);

    }

    private void fetchSongsList() {
        songsList = Utility.getSongsList();
    }

    public void pausePlaying() {
        Log.e("Calling", "I PAUSE PLAY");

        if(mediaPlayer!=null){

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

            createNotification(MyService.this, songDetails, isPlaying);
        }
    }

    public void playNextSong() {

        Log.e("Calling", "I NEXT CALLED");
        recyclerViewPosition = (recyclerViewPosition + 1) % songsList.size();
        Log.d("PLAYINGS", String.valueOf(recyclerViewPosition));
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

    public  void createNotification(Context context, SongDetails songDetails, boolean isPlaying) {
        Bitmap bm=null;
        Log.e("Calling","In CreateNotification");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");


            if (songDetails.playlistType.equalsIgnoreCase("Festival")) {
                bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.music_player);
            } else if(songDetails.playlistType.equalsIgnoreCase("Downloads")) {
                bm = BitmapFactory.decodeByteArray(songDetails.songAlbumArt, 0, songDetails.songAlbumArt.length);
            }

            PendingIntent pendingIntentPrevious;
            int drw_prvs;

            Intent intentPrevious = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_PREVIOUS);
            pendingIntentPrevious = PendingIntent.getBroadcast(context, 0,
                    intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
            drw_prvs = R.drawable.prev_btn;


            Intent intentPlay = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
                    intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
            int drw_play;
            if (isPlaying) {
                drw_play = R.drawable.pause_btn;
            } else {
                drw_play = R.drawable.play_btn;
            }

            PendingIntent pendingIntentNext;
            int drw_next;
            Intent intentNext = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_NEXT);
            pendingIntentNext = PendingIntent.getBroadcast(context, 0,
                    intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
            drw_next = R.drawable.next_btn;

            Intent intentOpen=new Intent(context, PlayerActivity.class);
            intentOpen.putExtra("position",-1);
            intentOpen.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingWholeClick = PendingIntent.getActivity(context, 0,
                    intentOpen, PendingIntent.FLAG_UPDATE_CURRENT);



            //create notifcation
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.player_logo_icon)
                    .setContentTitle(songDetails.getSongTitle())
                    .setContentText(songDetails.getSongDesc())
                    .setContentIntent(pendingWholeClick)
                    .setAutoCancel(true)
                    .setLargeIcon(bm)
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .addAction(drw_prvs, "Previous", pendingIntentPrevious)
                    .addAction(drw_play, "Play", pendingIntentPlay)
                    .addAction(drw_next, "Next", pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();

            notificationManagerCompat.notify(1, notification);
        }
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
