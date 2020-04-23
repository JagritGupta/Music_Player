package com.example.myplayer.SearchOption;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationActionService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        context.sendBroadcast(new Intent("SONG_LIST")
                .putExtra("actionname",intent.getAction()));

        Log.e("Calling","In NotificationActionService Called  ");
    }


}
