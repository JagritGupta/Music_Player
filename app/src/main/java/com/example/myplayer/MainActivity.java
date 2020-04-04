package com.example.myplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.UniversalTimeScale;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    RecyclerView recyclerView;
    TextView noSongsFound;
    MusicLibraryAdapter musicLibraryAdapter;
    ArrayList<SongDetails> songsList = null;
    ArrayList<SongDetails> songsFilterList = null;
    String artistName = "Artist Name";
    public static RelativeLayout miniPlayerLayout, miniSongInfoLayout;
    public static TextView miniSongTitle, miniSongDesc;
    public static ImageView miniPlayPauseBtn, miniSongImg;

    private final int[] festivalSongsList = {R.raw.aa_aaye_navratre_ambe_maa, R.raw.jai_jaikaar_sukhwinder_singh, R.raw.lali_lali_laal_chunariya, R.raw.navraton_mein_ghar_mere_aayi};

    public static ArrayList<File> mySongs;
    SongDetails songDetails;
    String menuType, typeOfPlaylist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songsList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        noSongsFound = findViewById(R.id.noDataFound);
        songDetails = new SongDetails();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent i = getIntent();
        menuType = i.getStringExtra("type");
        songsList = displaySongsInArrayList(menuType);
        Utility.setFestivalList(festivalSongsList);
        miniPlayerLayout = findViewById(R.id.mini_mediaPlayer);
        miniSongInfoLayout = findViewById(R.id.song_info_layout);
        miniPlayPauseBtn = findViewById(R.id.btn_pausePlay);
        miniSongDesc = findViewById(R.id.song_description);
        miniSongTitle = findViewById(R.id.song_name);
        miniSongImg = findViewById(R.id.song_imageView);


        musicLibraryAdapter = new MusicLibraryAdapter(this, songsList, typeOfPlaylist);
        Utility.setSongsList(songsList);
        SongDetails songDetails = new SongDetails(songsList);
        recyclerView.setAdapter(musicLibraryAdapter);

        miniSongInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    public static void miniPlayerAccess(Boolean isVisible, SongDetails songDetails) {
        if (isVisible) {
            miniPlayerLayout.setVisibility(View.VISIBLE);
            miniSongTitle.setText(songDetails.songTitle);
            miniSongDesc.setText(songDetails.songDesc);
            Bitmap bm = BitmapFactory.decodeFile(songDetails.songAlbumArt);
            miniSongImg.setImageBitmap(bm);
        }
    }


    public ArrayList<SongDetails> displaySongsInArrayList(String menuType) {
        switch (menuType) {
            case "Festival Songs":
                Toast.makeText(MainActivity.this, "FESTIVAL SONGS", Toast.LENGTH_SHORT).show();
                fetchFestivalSongs();
                return songsList;

            case "Favourites":
                Toast.makeText(MainActivity.this, "This Option is currently unavailable!", Toast.LENGTH_SHORT).show();
                return songsList;

            case "Downloads":
                Toast.makeText(MainActivity.this, "Downloads", Toast.LENGTH_SHORT).show();

                fetchDownloadSongs();

                /*mySongs = findSong(Environment.getExternalStorageDirectory());
                typeOfPlaylist = "Downloads";

                for (int i = 0; i < mySongs.size(); i++) {
                    SongDetails singleSong = new SongDetails(mySongs.get(i).getName(), artistName, R.id.songImage);
                    singleSong.setPath(mySongs.get(i).toString());
                    singleSong.position=-1;
                    singleSong.playlistType=typeOfPlaylist;
                    songsList.add(singleSong);
                }
*/
                if (mySongs.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    noSongsFound.setVisibility(View.VISIBLE);
                }
                Utility.setDownloadsList(mySongs);
                return songsList;

            case "AllSongs":
                Toast.makeText(MainActivity.this, "ALL SONGS", Toast.LENGTH_SHORT).show();

                fetchDownloadSongs();

                fetchFestivalSongs();

                typeOfPlaylist = "All Songs";

                if (songsList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    noSongsFound.setVisibility(View.VISIBLE);
                }
                return songsList;
        }


        return null;

    }

    public ArrayList<File> findSong(File file) {
        ArrayList<File> mySongs = new ArrayList<>();
        File[] files = file.listFiles();


        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    mySongs.addAll(findSong(singleFile));
                } else {
                    if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav") || singleFile.getName().endsWith(".m4a")) {
                        mySongs.add(singleFile); //Adding the mp3 and wav songs in the songsList

                    }
                }
            }
        }


        return mySongs;
    }

    public void fetchDownloadSongs() {
        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        final Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        int filePos = 0;
        mySongs = findSong(Environment.getExternalStorageDirectory());
        while (cursor.moveToNext()) {
            //final String albumName = mySongs.get(filePos).getName();
            final String albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            final String path = "";//cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
            final String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST));
            final String albumart = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART));
            SongDetails singleSong = new SongDetails(albumName, artist, R.id.songImage, albumart);
            singleSong.path=path;
            songsList.add(singleSong);
            filePos += 1;
        }
    }

    public void fetchFestivalSongs(){
        typeOfPlaylist = "Festival";
        Field[] fields = R.raw.class.getFields();
        Log.d("field", Integer.toString(fields.length));
        for (int i = 0; i < fields.length; i++) {
            Log.d("field", Integer.toString(i));
            String songName = fields[i].getName().replace("_", " ");
            SongDetails singleSong = new SongDetails(songName, artistName, R.id.songImage, "");
            singleSong.playlistType = typeOfPlaylist;
            singleSong.songID=festivalSongsList[i];
            singleSong.setPosition(i);
            songsList.add(singleSong);
        }
        if (fields.length == 0) {
            recyclerView.setVisibility(View.GONE);
            noSongsFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(this);
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        songsFilterList = new ArrayList<>();

        if (newText == null || newText.length() == 0) {
            noSongsFound.setVisibility(View.GONE);
            songsFilterList.addAll(songsList);
            musicLibraryAdapter.updateList(songsFilterList);

        }
        String filterPattern = newText.toLowerCase().trim();
        for (int i = 0; i < songsList.size(); i++) {
            SongDetails item = Utility.songsList.get(i);
            String songTitle = item.songTitle.toLowerCase().trim();
            Boolean check = songTitle.contains(filterPattern);
            if (check) {
                noSongsFound.setVisibility(View.GONE);
                songsFilterList.add(songsList.get(i));
                Toast.makeText(this, item.songTitle, Toast.LENGTH_SHORT).show();
            } else {
                noSongsFound.setVisibility(View.VISIBLE);
            }
        }
        if (songsFilterList != null) {
            musicLibraryAdapter.updateList(songsFilterList);
        }
        return true;
    }
}
