package com.example.myplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myplayer.Services.OnClearFromRecentServiceSearch;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity{
    Button prevBtn, nextBtn, next10, prev10;
    static ImageView setToUnfavouriteBtn, setToFavouriteBtn;
    ImageView pauseBtn, playSongImage;
    TextView songLabel, startTimer, endTimer;
    SeekBar seekBar;
    NotificationManager notificationManager;
    String sName;
    static MediaPlayer mediaPlayer;
    int currentPosition, recyclerViewPosition, totalDuration;
    ArrayList<SongDetails> songsList;
    boolean isFavouriteMenuMode = false;
    boolean isPlaying = true;
    Thread updateSeekBar;
    SongDetails songDetails;
    RoomViewModel viewModel;
    SongEntity song;
    private final int[] festivalSongsList = {R.raw.aa_aaye_navratre_ambe_maa, R.raw.jai_jaikaar_sukhwinder_singh, R.raw.lali_lali_laal_chunariya, R.raw.navraton_mein_ghar_mere_aayi};
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

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songsList = Utility.getSongsList();
        recyclerViewPosition = bundle.getInt("position");
        isFavouriteMenuMode = bundle.getBoolean("isFavouriteMenu");
        songDetails = Utility.songsList.get(recyclerViewPosition);
        displayIntoMediaPlayer(songDetails);
        playMedia(songDetails);

        //Create notification
        CreateNotification.createNotification(PlayerActivity.this, songDetails, isPlaying, 1, songsList.size() - 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("SONGS_LIST"));
            startService(new Intent(getBaseContext(), OnClearFromRecentServiceSearch.class));
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setTimer(currentPosition);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                setTimer(currentPosition);

            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setMax(mediaPlayer.getDuration());
                if (mediaPlayer.isPlaying()) {
                    pauseBtn.setImageResource(R.drawable.play_btn);
                    mediaPlayer.pause();
                    isPlaying = false;
                    CreateNotification.createNotification(PlayerActivity.this, songDetails,
                            isPlaying, recyclerViewPosition, songsList.size() - 1);

                } else {
                    pauseBtn.setImageResource(R.drawable.pause_btn);
                    isPlaying = true;
                    CreateNotification.createNotification(PlayerActivity.this, songDetails,
                            isPlaying, recyclerViewPosition, songsList.size() - 1);
                    mediaPlayer.start();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                recyclerViewPosition = (recyclerViewPosition + 1) % songsList.size();
                songDetails = songsList.get(recyclerViewPosition);
                pauseBtn.setImageResource(R.drawable.pause_btn);
                displayIntoMediaPlayer(songDetails);
                playMedia(songDetails);
                CreateNotification.createNotification(PlayerActivity.this, songDetails,
                        isPlaying, recyclerViewPosition, songsList.size() - 1);
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                recyclerViewPosition = (recyclerViewPosition - 1) % songsList.size();
                if (recyclerViewPosition < 0)
                    recyclerViewPosition = songsList.size() - 1;
                songDetails = songsList.get(recyclerViewPosition);
                pauseBtn.setImageResource(R.drawable.pause_btn);
                displayIntoMediaPlayer(songDetails);
                playMedia(songDetails);
                CreateNotification.createNotification(PlayerActivity.this, songDetails,
                        isPlaying, recyclerViewPosition, songsList.size() - 1);
            }
        });


        next10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPosition = currentPosition + 10000;
                if (currentPosition >= totalDuration) {
                    currentPosition = totalDuration;
                    seekBar.setProgress(currentPosition);
                }
                seekBar.setProgress(currentPosition);
                setTimer(currentPosition);
                mediaPlayer.seekTo(currentPosition);
            }
        });

        prev10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = currentPosition - 10000;
                if (currentPosition <= 0) {
                    currentPosition = 0;
                    seekBar.setProgress(currentPosition);

                }
                seekBar.setProgress(currentPosition);
                setTimer(currentPosition);
                mediaPlayer.seekTo(currentPosition);
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
                song = new SongEntity(songDetails);
                viewModel = ViewModelProviders.of(PlayerActivity.this).get(RoomViewModel.class);
                viewModel.insert(song);

            }
        });

        setToUnfavouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PlayerActivity.this, "Song removed from your favourites", Toast.LENGTH_SHORT).show();
                songDetails.setIsFavourite(false);
                Utility.songsList.get(recyclerViewPosition).setIsFavourite(true);
                viewModel = ViewModelProviders.of(PlayerActivity.this).get(RoomViewModel.class);
                SongEntity song = new SongEntity(songDetails);
                viewModel.deleteSong(song);
                setToFavouriteBtn.setVisibility(View.VISIBLE);
                setToUnfavouriteBtn.setVisibility(View.GONE);
            }
        });
    }


    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID, "JG", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onResume();
        MainActivity.miniPlayerAccess(true, songDetails);

        if (isFavouriteMenuMode == true && !songDetails.isFavourite()) {
            songsList.remove(songDetails);
            Utility.setSongsList(songsList);
            //MusicLibraryAdapter musicLibraryAdapter=new MusicLibraryAdapter(PlayerActivity.this,songsList,"");
            //musicLibraryAdapter.updateList(songsList);
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

    public void playMedia(SongDetails songDetails) {
        Intent serviceIntent = new Intent(PlayerActivity.this, MyService.class);

        if (songDetails.playlistType == "Festival") {                     //type FESTIVAL

            mediaPlayer = MediaPlayer.create(getApplicationContext(), festivalSongsList[songDetails.getPosition()]);
            serviceIntent.putExtra("mediaPlayer", festivalSongsList[songDetails.position]);
            mediaPlayer.start();

        } else {            //type DOWNLOADS

            Uri uri = Uri.parse(songDetails.path);
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            serviceIntent.putExtra("mediaPlayer", songDetails.getPath());

            mediaPlayer.start();

        }
        startService(serviceIntent);
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setProgress(0);
        callResetSeekBar();
        setEndTimer(mediaPlayer.getDuration());
        updateSeekBar.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        if (currentPosition == totalDuration) {
            nextBtn.performClick();
        }
    }

    public void displayIntoMediaPlayer(SongDetails songDetails) {

        sName = songDetails.songTitle;
        String songName = songDetails.songTitle;
        sName.replace(".mp3", "").replace(".wav", "");
        songName.replace(".mp3", "").replace(".wav", "");
        songLabel.setText(songName);
        songLabel.setSelected(true);
        Toast.makeText(PlayerActivity.this, songName, Toast.LENGTH_LONG).show();
        if (songDetails.playlistType == "Festival") {
            playSongImage.setImageResource(R.drawable.music_player);
        } else {
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


    //Below func are for Notifcations usage

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

    @Override
    protected void onStop() {
        mediaPlayer.stop();
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
    }

}
