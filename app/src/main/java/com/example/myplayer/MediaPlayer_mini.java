package com.example.myplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MediaPlayer_mini extends AppCompatActivity {
    ImageView songImage,playPauseBtn;
    TextView songLabel,songDesc;
    SongDetails songDetails;

    public MediaPlayer_mini(SongDetails songDetails) {
        this.songDetails = songDetails;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player_mini);

        songImage=findViewById(R.id.song_imageView);
        playPauseBtn=findViewById(R.id.btn_pausePlay);
        songLabel=findViewById(R.id.song_name);
        songDesc=findViewById(R.id.song_description);

    }
}
