package com.example.myplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaMetadataRetriever;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.lang.reflect.Field;

import javax.security.auth.callback.Callback;

public class MainMenu extends AppCompatActivity {

    TextView festivalSongs;
    TextView favourites;
    TextView downloads;
    TextView allSongs;
    RoomService roomService;
    private final int[] festivalSongsList = {R.raw.aa_aaye_navratre_ambe_maa, R.raw.jai_jaikaar_sukhwinder_singh, R.raw.lali_lali_laal_chunariya, R.raw.navraton_mein_ghar_mere_aayi};
    static MyService myService;
    private boolean isServiceBound;
    Intent serviceIntent;
    private ServiceConnection serviceConnection;
    private static int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        festivalSongs = findViewById(R.id.festiveSongs);
        favourites = findViewById(R.id.favourites);
        downloads = findViewById(R.id.downloads);
        allSongs = findViewById(R.id.allSongs);

    }


    @Override
    protected void onResume() {
        super.onResume();
        runTimePermission();
    }

    public void runTimePermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        fetchDownloadSongs(Environment.getExternalStorageDirectory());
                        fetchFestivalSongs();
                        Utility.setFestivalList(festivalSongsList);

                        serviceIntent = new Intent(getApplicationContext(), MyService.class);
                        serviceIntent.putExtra("SongPos", 0);
                        serviceIntent.putExtra("ReadyToPlay", false);
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(serviceIntent);
                        }*/
                        startService(serviceIntent);
                        bindMyService();


                        festivalSongs.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(MainMenu.this, MainActivity.class);
                                i.putExtra("type", "Festival Songs");
                                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);

                            }
                        });

                        favourites.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent i = new Intent(MainMenu.this, MainActivity.class);
                                i.putExtra("type", "Favourites");
                                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);

                            }
                        });

                        downloads.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent i = new Intent(MainMenu.this, MainActivity.class);
                                i.putExtra("type", "Downloads");
                                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);

                            }
                        });

                        allSongs.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(MainMenu.this, MainActivity.class);
                                i.putExtra("type", "AllSongs");
                                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);

                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void fetchFestivalSongs() {
        Field[] fields = R.raw.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            String songName = fields[i].getName().replace("_", " ");
            SongDetails singleSong = new SongDetails(songName, "ArtistName");
            singleSong.setPlaylistType("Festival");
            singleSong.setPosition(i);
            singleSong.setPath(Integer.toString(festivalSongsList[i]));

            insertIntoDB(singleSong);
        }
    }

    public void fetchFavouriteSongs() {

    }

    public void fetchDownloadSongs(File file) {

        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    fetchDownloadSongs(singleFile);
                } else {
                    try {
                        if ((singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav") || singleFile.getName().endsWith(".m4a"))) {

                            String path = singleFile.getPath();
                            MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
                            metaRetriver.setDataSource(path);
                            Log.d("SONGS", String.valueOf(singleFile.length()));

                            String albumName = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                            Log.d("NAMESS", albumName);
                            if(albumName==null|| albumName==""){
                                albumName="Unknown Song";
                            }

                            String artistName = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            if(artistName.length()<=0){
                                artistName="Unknown artist";
                            }

                            byte[] albumArt = metaRetriver.getEmbeddedPicture();

                            SongDetails singleSong = new SongDetails(albumName, artistName, albumArt);
                            singleSong.setPath(path);
                            singleSong.setPosition(pos);
                            pos++;
                            singleSong.setPlaylistType("Downloads");
                            if(albumName.length()>0) {
                                insertIntoDB(singleSong);
                            }
                        }
                    } catch (Exception e) {
                        Log.d("Offoo", "Error");
                    }
                }
            }
        }
    }

    public void insertIntoDB(SongDetails songDetails) {
        roomService = new RoomService(getApplication());

        roomService.insert(songDetails);

    }

    public boolean bindMyService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {
                isServiceBound = true;
                MyService.MyServiceBinder myServiceBinder = (MyService.MyServiceBinder) iBinder;
                myService = myServiceBinder.getService();
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
}
