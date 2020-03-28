package com.example.myplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {
    Button prevBtn, nextBtn, next10, prev10;
    ImageView pauseBtn, favouriteBtn, unFavouriteBtn;
    TextView songLabel, startTimer, endTimer;
    SeekBar seekBar;
    String sName, typeOfPlaylist;
    static MediaPlayer mediaPlayer;
    int currentPosition, position, totalDuration;
    ArrayList<SongDetails> songsList;
    Thread updateSeekBar;
    SongDetails songDetails;
    private final int[] festivalSongsList = {R.raw.aa_aaye_navratre_ambe_maa, R.raw.jai_jaikaar_sukhwinder_singh, R.raw.lali_lali_laal_chunariya, R.raw.navraton_mein_ghar_mere_aayi};


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
        favouriteBtn = findViewById(R.id.favFilled);
        unFavouriteBtn = findViewById(R.id.favUnfilled);
        nextBtn = findViewById(R.id.btn_next);
        startTimer = findViewById(R.id.start_timer);
        endTimer = findViewById(R.id.end_timer);
        songLabel = findViewById(R.id.song_name_label);
        seekBar = findViewById(R.id.seek_bar);
        next10 = findViewById(R.id.forward_ten);
        prev10 = findViewById(R.id.prev_ten);
        songDetails = SongDetails.getInstance();
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
        songsList = SongDetails.getInstance().getSongsList();
        position = songDetails.position;
        songDetails = songsList.get(position);
        typeOfPlaylist = bundle.getString("typeOfPlaylist");
        playMedia(typeOfPlaylist, position);


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
                } else {
                    pauseBtn.setImageResource(R.drawable.pause_btn);
                    mediaPlayer.start();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = (position + 1) % songsList.size();
                songDetails = songsList.get(position);
                playMedia(typeOfPlaylist, position);
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = (position - 1) % songsList.size();
                if (position < 0)
                    position = songsList.size() - 1;
                songDetails = songsList.get(position);
                playMedia(typeOfPlaylist, position);
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

        unFavouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PlayerActivity.this, "Song Added to your favourites", Toast.LENGTH_SHORT).show();
                unFavouriteBtn.setVisibility(View.GONE);
                favouriteBtn.setVisibility(View.VISIBLE);
            }
        });

        favouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PlayerActivity.this, "Song removed from your favourites", Toast.LENGTH_SHORT).show();
                unFavouriteBtn.setVisibility(View.VISIBLE);
                favouriteBtn.setVisibility(View.GONE);
            }
        });
    }

    public void callSeekBar() {
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

    public void setTimer(long millis){
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        startTimer.setText(hms);
    }

    public void setEndTimer(long millis){
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        endTimer.setText(hms);
    }

    public void playMedia(String type, int position) {

        sName = songDetails.songTitle;
        String songName = songDetails.songTitle;
        sName.replace(".mp3", "").replace(".wav", "");
        songName.replace(".mp3", "").replace(".wav", "");
        songLabel.setText(songName);
        songLabel.setSelected(true);

        switch (type) {
            case "Downloads":
                Uri uri = Uri.parse(songDetails.toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                setEndTimer(mediaPlayer.getDuration());
                updateSeekBar.start();
                seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
                if (currentPosition == totalDuration) {
                    nextBtn.performClick();
                }
                break;

            case "Festival":
                mediaPlayer = MediaPlayer.create(getApplicationContext(),festivalSongsList[position]);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setProgress(0);
                callSeekBar();
                setEndTimer(mediaPlayer.getDuration());
                updateSeekBar.start();
                seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
                if (currentPosition == totalDuration) {
                    nextBtn.performClick();
                }
                break;

        }
    }

    @Override
    protected void onStop() {
        mediaPlayer.stop();
        super.onStop();
    }
}
