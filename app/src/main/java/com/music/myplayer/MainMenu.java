package com.music.myplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
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

public class MainMenu extends AppCompatActivity {

    TextView festivalSongs;
    TextView playlists;
    TextView downloads;
    TextView allSongs;
    RoomService roomService;
    private final int[] festivalSongsList = {R.raw.ek_ajnabee_haseena_se,R.raw.saamne_yeh_kaun_aaya,R.raw.tum_jo_mil_gaye_ho,
            R.raw.likhe_jo_khat_tujhe_sanam,R.raw.yeh_raaten_yeh_mausam_sanam,R.raw.kasam_ki_kasam_unplugged,
            R.raw.kajra_mohobbat_wala,
            R.raw.gulabi_aankhen_sanam,R.raw.mere_rang_mein,R.raw.raah_mein_unse_mulaqat_ho_gayi};

    String[] festivalNames={
            "Ek ajnabee haseena se",
            "Samne yeh kaun aaya",
            "Tum Jo mil Gaye Ho",
            "Likhe Jo Khat Tujhe",
            "Yeh Raaten Yeh Mausam",
            "Kasam Ki Kasam",
            "Kajra Mohobbat wala",
            "Gulabi Ankhen-Sanam",
            "Mere Range Mein",
            "Raah Mein Unse Mulaqat Hogyi"};
    static MyService myService;
    private boolean isServiceBound;
    Intent serviceIntent;
    private ServiceConnection serviceConnection;
    private static int pos = 0;

    int[] festivalPics= new int[]{R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4, R.drawable.a5,
            R.drawable.a6, R.drawable.a7, R.drawable.a8, R.drawable.a9, R.drawable.a10};
    int festivalCounter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        festivalSongs = findViewById(R.id.festiveSongs);
        playlists = findViewById(R.id.favourites);
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

                        playlists.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent i = new Intent(MainMenu.this, PlaylistActivity.class);
                                i.putExtra("type", "MY PLAYLISTS");
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
            String songName = festivalNames[i];
            SongDetails singleSong = new SongDetails(songName, "App's Special");
            singleSong.setPlayerType("Festival");
            singleSong.setPosition(i);
            singleSong.setFestiwalDrawable(festivalPics[i]);
            singleSong.setPath(Integer.toString(festivalSongsList[i]));
            singleSong.setIsFavourite(false);
            insertIntoDB(singleSong);
        }
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
                            String albumName=singleFile.getName();
                            albumName=albumName.replace(".mp3","");
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
                            singleSong.setIsFavourite(false);
                            pos++;
                            singleSong.setPlayerType("Downloads");
                            if(albumArt!=null && albumArt.length>0) {
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
