package com.example.myplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
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
import java.util.HashMap;
import java.util.List;

import static com.example.myplayer.CONSTANTS.ACTION_PAUSE;
import static com.example.myplayer.CONSTANTS.ACTION_PLAY;
import static java.util.Collections.sort;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    RecyclerView recyclerView;
    TextView noSongsFound;
    MusicLibraryAdapter musicLibraryAdapter;
    ArrayList<SongDetails> songsList = null;
    ArrayList<SongDetails> songsFilterList = null;
    HashMap<String, String> songFavMap = null;
    public static RelativeLayout miniPlayerLayout, miniSongInfoLayout;
    public static TextView miniSongTitle, miniSongDesc;
    public static ImageView miniPlayPauseBtn, miniSongImg;
    static int pos = 0;
    RoomService roomService;
    static MyService myService;
    public static boolean isMainActivityVisible = false;
    private final int[] festivalSongsList = {R.raw.aa_aaye_navratre_ambe_maa, R.raw.jai_jaikaar_sukhwinder_singh, R.raw.lali_lali_laal_chunariya, R.raw.navraton_mein_ghar_mere_aayi};

    static SongDetails songDetails;
    String menuType, typeOfPlaylist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isMainActivityVisible = true;
        songsList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        noSongsFound = findViewById(R.id.noDataFound);
        songDetails = new SongDetails();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent i = getIntent();
        menuType = i.getStringExtra("type");
        songsList = displaySongsInArrayList(menuType);
        myService = MainMenu.myService;
        Utility.setFestivalList(festivalSongsList);
        miniPlayerLayout = findViewById(R.id.mini_mediaPlayer);
        miniSongInfoLayout = findViewById(R.id.song_info_layout);
        miniPlayPauseBtn = findViewById(R.id.btn_pausePlay);
        miniSongDesc = findViewById(R.id.song_description);
        miniSongTitle = findViewById(R.id.song_name);
        miniSongImg = findViewById(R.id.song_imageView);
        Utility.setSongsList(songsList);
        musicLibraryAdapter = new MusicLibraryAdapter(this, songsList, typeOfPlaylist, getApplication());
        recyclerView.setAdapter(musicLibraryAdapter);


        miniSongInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PlayerActivity.class);
                i.putExtra("position", -1);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                isMainActivityVisible = false;
                startActivity(i);
            }
        });

        miniPlayPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myService.pausePlaying();
                if (myService.isMediaPlaying()) {
                    miniPlayPauseBtn.setImageResource(R.drawable.pause_btn);
                } else {
                    miniPlayPauseBtn.setImageResource(R.drawable.play_btn);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isMainActivityVisible = false;
    }

    public static void miniPlayerAccess() {
        MediaPlayer mediaPlayer = myService.getMediaPlayer();
        if (mediaPlayer != null) {
            songDetails = myService.getCurrentSongObject();
            miniPlayerLayout.setVisibility(View.VISIBLE);
            miniSongTitle.setText(songDetails.songTitle);
            miniSongDesc.setText(songDetails.songDesc);
            if (mediaPlayer.isPlaying()) {
                miniPlayPauseBtn.setImageResource(R.drawable.pause_btn);
            } else {
                miniPlayPauseBtn.setImageResource(R.drawable.play_btn);
            }

            if (songDetails.playlistType.equalsIgnoreCase("Festival")) {
                miniSongImg.setImageResource(R.drawable.music_player);
            } else {
                Bitmap bm = BitmapFactory.decodeByteArray(songDetails.songAlbumArt, 0, songDetails.songAlbumArt.length);
                miniSongImg.setImageBitmap(bm);
            }
        }

    }

    @Override
    protected void onResume() {
        isMainActivityVisible = true;
        miniPlayerAccess();
        super.onResume();
        musicLibraryAdapter.notifyDataSetChanged();
    }

    public ArrayList<SongDetails> displaySongsInArrayList(String menuType) {

        roomService = new RoomService(getApplication());
        switch (menuType) {
            case "Festival Songs":
                Toast.makeText(MainActivity.this, "FESTIVAL SONGS", Toast.LENGTH_SHORT).show();

                songsList = fetchFestivalFromDB();

                if (songsList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    noSongsFound.setVisibility(View.VISIBLE);
                }
                return songsList;

            case "Favourites":
                Toast.makeText(MainActivity.this, "Your favourites", Toast.LENGTH_SHORT).show();
                songFavMap = new HashMap<String, String>();
                new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);   //Enables swipe to delete feature
                songsList = fetchFavouritesFromDB();
                typeOfPlaylist = "Favourites";


                if (songsList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    noSongsFound.setVisibility(View.VISIBLE);
                }
                return songsList;

            case "Downloads":
                Toast.makeText(MainActivity.this, "Downloads", Toast.LENGTH_SHORT).show();
                pos = 0;
                songFavMap = new HashMap<>();
                //fetchFavouriteSongs();
                songsList = new ArrayList<>();
                songsList = fetchDownloadFromDB();

                if (songsList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    noSongsFound.setVisibility(View.VISIBLE);
                }
                return songsList;

            case "AllSongs":
                Toast.makeText(MainActivity.this, "ALL SONGS", Toast.LENGTH_SHORT).show();

                songsList = fetchAllSongsFromDB();

                typeOfPlaylist = "All Songs";
                if (songsList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    noSongsFound.setVisibility(View.VISIBLE);
                }
                return songsList;
        }
        return null;
    }


    public ArrayList<SongDetails> fetchFestivalFromDB() {
        List<SongDetails> songsList = roomService.fetchFestivals();

        return (ArrayList) songsList;
    }

    public ArrayList<SongDetails> fetchFavouritesFromDB() {
        List<SongDetails> songsList = roomService.fetchFavourites();
        return (ArrayList) songsList;
    }

    public ArrayList<SongDetails> fetchDownloadFromDB() {
        List<SongDetails> songsList = roomService.fetchDownloads();

        return (ArrayList) songsList;
    }

    public ArrayList<SongDetails> fetchAllSongsFromDB() {
        List<SongDetails> songsList = roomService.fetchAllSongs();

        return (ArrayList) songsList;
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



    //Feature of delete item on Swipe in the Favourite Options
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            SongDetails songDetails = songsList.get(viewHolder.getAdapterPosition());
            roomService.deleteSong(songDetails);
            songsList.remove(songDetails);
            Utility.setSongsList(songsList);
            musicLibraryAdapter.notifyDataSetChanged();
        }
    };
}
