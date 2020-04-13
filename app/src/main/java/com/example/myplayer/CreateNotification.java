package com.example.myplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myplayer.Notification.NotificationActionService;

public class CreateNotification {
    public static final String CHANNEL_ID = "channel1";
    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static Notification notification;

    public static void createNotification(Context context, SongDetails songDetails, boolean isPlaying, int pos, int size) {
        Bitmap bm;
        Log.e("yups","In CreateNotification");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Toast.makeText(context, "Integer.toString(Build.LLLLVERSION.SDK_INT)", Toast.LENGTH_LONG).show();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");


            if (songDetails.playlistType == "Festival") {
                bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.music_player);
            } else {
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


            //create notifcation
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.music_player)
                    .setContentTitle(songDetails.getSongTitle())
                    .setContentText(songDetails.getSongDesc())
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

}
