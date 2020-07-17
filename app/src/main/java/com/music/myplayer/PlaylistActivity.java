package com.music.myplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity implements MusicLibraryAdapter.IconnectPlaylist {

    RecyclerView playlistRecyclerView;
    TextView noPlaylistFound;
    MusicLibraryAdapter musicAdapter;
    private RoomService roomService;
    List<String> listOfPlaylists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        playlistRecyclerView = findViewById(R.id.playlist_recycler_view);
        noPlaylistFound=findViewById(R.id.noPlaylistFound);
        roomService = new RoomService(getApplication());
        playlistRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        listOfPlaylists = Utility.getAllPlaylists(roomService);
        musicAdapter = new MusicLibraryAdapter(this, listOfPlaylists, getApplication(), PlaylistActivity.this, "Playlist", getSupportFragmentManager());
        musicAdapter.connectingWithInterface(this);
        playlistRecyclerView.setAdapter(musicAdapter);

        if(listOfPlaylists.size()==0){
            noPlaylistFound.setVisibility(View.VISIBLE);
        }
        else
            noPlaylistFound.setVisibility(View.GONE);
    }


    @Override
    public void sendSongsPlaylist(ArrayList<SongDetails> songsList) {
        Intent i = new Intent(PlaylistActivity.this, MainActivity.class);
        i.putExtra("type", "PLAYLISTS");
        i.putExtra("songsList", songsList);
        Utility.setSongsList(songsList);
        startActivity(i);
    }

}