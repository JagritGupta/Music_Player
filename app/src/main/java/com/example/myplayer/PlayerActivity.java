package com.example.myplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myplayer.Notification.OnClearFromRecentServiceSearch;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {
    Button prevBtn, nextBtn, next10, prev10;
    static ImageView setToUnfavouriteBtn, setToFavouriteBtn;
    ImageView pauseBtn, playSongImage;
    TextView songLabel, startTimer, endTimer;
    SeekBar seekBar;
    NotificationManager notificationManager;
    String sName;
    MediaPlayer mediaPlayer;
    int currentPosition, recyclerViewPosition, totalDuration;
    ArrayList<SongDetails> songsList;
    boolean isFavouriteMenuMode = false;
    boolean isPlaying = true;
    Thread updateSeekBar;
    SongDetails songDetails;
    RoomService roomService;
    ServiceConnection serviceConnection = null;
    private final int[] festivalSongsList = {R.raw.aa_aaye_navratre_ambe_maa, R.raw.jai_jaikaar_sukhwinder_singh, R.raw.lali_lali_laal_chunariya, R.raw.navraton_mein_ghar_mere_aayi};
    boolean isServiceBound = false;
    MyService myService;
    Intent serviceIntent;
    //Hash map
    //room-->id,name(hash map)

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        pauseBtn = findViewById(R.id.btn_pause);
        prevBtn = findViewById(R.id.btn_prev);
        setToUnfavouriteBtn = findViewById(R.id.favFilled);
        setToFavouriteBtn = findViewById(R.id.favUnfilled);
        playSongImage = findViewById(R.id.playSong_image);
        nextBtn = findViewById(R.id.btn_next);
        startTimer = findViewById(R.id.start_timer);
        endTimer = findViewById(R.id.end_timer);
        songLabel = findViewById(R.id.song_name_label);
        seekBar = findViewById(R.id.seek_bar);
        next10 = findViewById(R.id.forward_ten);
        prev10 = findViewById(R.id.prev_ten);
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        roomService = new RoomService(getApplication());
        serviceIntent = new Intent(getApplicationContext(), MyService.class);
        registerReceiver(broadcastReceiver, new IntentFilter("SONGS_LIST"));

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setMax(myService.getTotalDuration());
                myService.pausePlaying();
                if (myService.isMediaPlaying()) {
                    pauseBtn.setImageResource(R.drawable.pause_btn);
                } else {
                    pauseBtn.setImageResource(R.drawable.play_btn);
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEndTimer(myService.getTotalDuration());
                pauseBtn.setImageResource(R.drawable.pause_btn);
                myService.playNextSong();
                displayIntoMediaPlayer(myService.getCurrentSongObject());
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEndTimer(myService.getTotalDuration());
                pauseBtn.setImageResource(R.drawable.pause_btn);
                myService.playPreviousSong();
                displayIntoMediaPlayer(myService.getCurrentSongObject());
            }
        });

        updateSeekBar = new Thread() {
            @Override
            public void run() {
                super.run();
                totalDuration = myService.getTotalDuration();
                currentPosition = 0;

                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = myService.getCurrentPlayerPosition();
                        seekBar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songsList = Utility.getSongsList();
        recyclerViewPosition = bundle.getInt("position");
        isFavouriteMenuMode = bundle.getBoolean("isFavouriteMenu");
        songDetails = songsList.get(recyclerViewPosition);


        displayIntoMediaPlayer(songDetails);
        playMedia(songDetails);

       /* //Create notification
        CreateNotification.createNotification(PlayerActivity.this, songDetails, isPlaying, 1, songsList.size() - 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("SONGS_LIST"));
            startService(new Intent(getBaseContext(), OnClearFromRecentServiceSearch.class));
        }*/

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setTimer(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myService.changeSeekBarPosition(seekBar.getProgress());
                setTimer(myService.getCurrentPlayerPosition());

            }
        });

        next10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myService.next10();
                currentPosition = myService.getCurrentPlayerPosition();
                seekBar.setProgress(currentPosition);
                setTimer(currentPosition);
            }
        });

        prev10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myService.prev10sec();
                currentPosition = myService.getCurrentPlayerPosition();
                seekBar.setProgress(currentPosition);
                setTimer(currentPosition);
            }
        });

        setToFavouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PlayerActivity.this, "Song Added to your favourites", Toast.LENGTH_SHORT).show();
                setToFavouriteBtn.setVisibility(View.GONE);
                setToUnfavouriteBtn.setVisibility(View.VISIBLE);
                songDetails.setIsFavourite(true);
                Utility.songsList.get(recyclerViewPosition).setIsFavourite(true);
                //Code to insert
                roomService.insert(songDetails);

            }
        });

        setToUnfavouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PlayerActivity.this, "Song removed from your favourites", Toast.LENGTH_SHORT).show();
                songDetails.setIsFavourite(false);
                Utility.songsList.get(recyclerViewPosition).setIsFavourite(true);
                roomService.deleteSong(songDetails);
                setToFavouriteBtn.setVisibility(View.VISIBLE);
                setToUnfavouriteBtn.setVisibility(View.GONE);
            }
        });
    }


    public void playMedia(SongDetails songDetails) {
        serviceIntent.putExtra("SongPos", recyclerViewPosition);
        startService(serviceIntent);
        isServiceBound = bindMyService();

        seekBar.setProgress(0);
        //callResetSeekBar();
        //updateSeekBar.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);

    }

    public void displayIntoMediaPlayer(SongDetails songDetails) {

        sName = songDetails.songTitle;
        String songName = songDetails.songTitle;
        sName.replace(".mp3", "").replace(".wav", "");
        songName.replace(".mp3", "").replace(".wav", "");
        songLabel.setText(songName);
        songLabel.setSelected(true);
        Toast.makeText(PlayerActivity.this, songName, Toast.LENGTH_LONG).show();

        if (songDetails.playlistType.equalsIgnoreCase("Festival")) {
            playSongImage.setImageResource(R.drawable.music_player);
        } else if (songDetails.playlistType.equalsIgnoreCase("Downloads")) {
            Bitmap bm = BitmapFactory.decodeByteArray(songDetails.songAlbumArt, 0, songDetails.songAlbumArt.length);
            playSongImage.setImageBitmap(bm);
        }

        if (songDetails.isFavourite()) {
            setToFavouriteBtn.setVisibility(View.GONE);
            setToUnfavouriteBtn.setVisibility(View.VISIBLE);
        } else {
            setToFavouriteBtn.setVisibility(View.VISIBLE);
            setToUnfavouriteBtn.setVisibility(View.GONE);
        }
    }

    public boolean bindMyService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {
                isServiceBound = true;
                MyService.MyServiceBinder myServiceBinder = (MyService.MyServiceBinder) iBinder;
                myService = myServiceBinder.getService();
                seekBar.setMax(myService.getTotalDuration());
                setEndTimer(myService.getTotalDuration());
                updateSeekBar.start();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isServiceBound = false;
                myService = null;
            }
        };
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
        return isServiceBound;
    }

    public void unBindService() {
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
        }
    }


    /*private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID, "JG", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }

        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onResume();
        MainActivity.miniPlayerAccess(songDetails, true);

        if (isFavouriteMenuMode == true && !songDetails.isFavourite()) {
            songsList.remove(songDetails);
            Utility.setSongsList(songsList);
        }
    }

    public void callResetSeekBar() {
        updateSeekBar = new Thread() {
            @Override
            public void run() {
                super.run();
                totalDuration = mediaPlayer.getDuration();
                currentPosition = 0;

                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public void setTimer(long millis) {
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        startTimer.setText(hms);
    }

    public void setEndTimer(long millis) {
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        endTimer.setText(hms);
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action) {
                case CreateNotification.ACTION_PREVIOUS:
                    Log.e("yups", "In playerActivity  ");
                    prevBtn.performClick();
                    break;

                case CreateNotification.ACTION_NEXT:
                    nextBtn.performClick();
                    break;

                case CreateNotification.ACTION_PLAY:
                    pauseBtn.performClick();
                    break;
            }
        }
    };

}
