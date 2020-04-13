package com.example.myplayer.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationActionService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("SONGS_LIST")
        .putExtra("actionname",intent.getAction()));
        Log.e("yups","In NotificationActionService  ");

    }
}
