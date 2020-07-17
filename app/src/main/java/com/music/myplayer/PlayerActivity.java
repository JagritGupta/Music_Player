package com.music.myplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {
    public static Button prevBtn, nextBtn;
    Button next10, prev10;
    ImageView playSongImage, createPlaylist;
    static ImageView pauseBtn;
    public static boolean isPlayerActivityActive = false;
    TextView songLabel, startTimer, endTimer;
    SeekBar seekBar;
    String sName;
    int currentPosition, recyclerViewPosition, totalDuration;
    ArrayList<SongDetails> songsList;
    boolean isFavouriteMenuMode = false;
    boolean isPlaying = true;
    Thread updateSeekBar;
    SongDetails songDetails;
    RoomService roomService;
    ServiceConnection serviceConnection = null;
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
        isPlayerActivityActive = true;
        pauseBtn = findViewById(R.id.btn_pause);
        prevBtn = findViewById(R.id.btn_prev);
        playSongImage = findViewById(R.id.playSong_image);
        nextBtn = findViewById(R.id.btn_next);
        startTimer = findViewById(R.id.start_timer);
        endTimer = findViewById(R.id.end_timer);
        songLabel = findViewById(R.id.song_name_label);
        createPlaylist =findViewById(R.id.playlistBtn);
        seekBar = findViewById(R.id.seek_bar);
        next10 = findViewById(R.id.forward_ten);
        prev10 = findViewById(R.id.prev_ten);
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        roomService = new RoomService(getApplication());
        serviceIntent = new Intent(getApplicationContext(), MyService.class);
        myService = MainMenu.myService;

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

                    currentPosition = myService.getCurrentPlayerPosition();
                    seekBar.setProgress(currentPosition);


                }

                if (myService.getCurrentPlayerPosition() >= myService.getTotalDuration()) {
                    nextBtn.performClick();
                }

            }
        };


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songsList = Utility.getSongsList();
        recyclerViewPosition = bundle.getInt("position");
        isFavouriteMenuMode = bundle.getBoolean("isFavouriteMenu");
        if (recyclerViewPosition != -1) {           //fresh start with PlayerActivity
            songDetails = songsList.get(recyclerViewPosition);
            playMedia(songDetails);
            displayIntoMediaPlayer(songDetails);
        } else {
            songDetails = myService.getCurrentSongObject();
            displayIntoMediaPlayer(songDetails);

        }


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

        createPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewPlaylistDialog createNewPlaylistDialog=new CreateNewPlaylistDialog(getApplication(),songDetails);
                createNewPlaylistDialog.show(getSupportFragmentManager(),"");
            }
        });
    }


    public void playMedia(SongDetails songDetails) {
        myService.playMedia(recyclerViewPosition);
    }

    public void displayIntoMediaPlayer(SongDetails songDetails) {

        sName = songDetails.songTitle;
        String songName = songDetails.songTitle;
        sName.replace(".mp3", "").replace(".wav", "");
        songName.replace(".mp3", "").replace(".wav", "");
        songLabel.setText(songName);
        songLabel.setSelected(true);

        seekBar.setMax(myService.getTotalDuration());
        setTimer(myService.getCurrentPlayerPosition());
        setEndTimer(myService.getTotalDuration());

        if (myService.isMediaPlaying()) {
            pauseBtn.setImageResource(R.drawable.pause_btn);
        } else {
            pauseBtn.setImageResource(R.drawable.play_btn);
        }

        callResetSeekBar();
        updateSeekBar.start();
        seekBar.setProgress(myService.getCurrentPlayerPosition());
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.iconColor), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);

        if (songDetails.playerType.equalsIgnoreCase("Festival")) {
            playSongImage.setImageResource(songDetails.getFestiwalDrawable());
        } else if (songDetails.playerType.equalsIgnoreCase("Downloads")) {
            if(songDetails.songAlbumArt!=null && songDetails.songAlbumArt.length>0) {
                Bitmap bm = BitmapFactory.decodeByteArray(songDetails.songAlbumArt, 0, songDetails.songAlbumArt.length);
                playSongImage.setImageBitmap(bm);
            }
        }

    }

    public void unBindService() {
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
        }
    }


    @Override
    protected void onResume() {

        isPlayerActivityActive = true;
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onResume();
        isPlayerActivityActive = false;
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

                if (myService.getCurrentPlayerPosition() >= myService.getTotalDuration()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nextBtn.performClick();
                        }
                    });
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
                    prevBtn.performClick();
                    break;

                case CreateNotification.ACTION_NEXT:
                    Log.e("yups", "In playerActivity  ");
                    nextBtn.performClick();
                    break;

                case CreateNotification.ACTION_PLAY:
                    pauseBtn.performClick();
                    break;
            }
        }
    };

}
