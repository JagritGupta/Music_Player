package com.example.myplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView noSongsFound;
    MusicLibraryAdapter musicLibraryAdapter;
    ArrayList<SongDetails> songsList =null;
    String artistName="Artist Name";
    ArrayList<File> mySongs;
    SongDetails songDetails;
    String menuType,typeOfPlaylist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songsList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        noSongsFound=findViewById(R.id.noDataFound);
        songDetails=SongDetails.getInstance();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent i=getIntent();
        menuType=i.getStringExtra("type");
        songsList =displaySongsInArrayList(menuType);



        musicLibraryAdapter = new MusicLibraryAdapter(this, songsList,typeOfPlaylist);
        SongDetails songDetails=new SongDetails(songsList);
        recyclerView.setAdapter(musicLibraryAdapter);




    }



    public ArrayList<SongDetails> displaySongsInArrayList(String menuType) {
        switch (menuType){
            case "Festival Songs":
                Toast.makeText(MainActivity.this,"FESTIVAL SONGS",Toast.LENGTH_SHORT).show();
                Field[] fields=R.raw.class.getFields();
                Log.d("field",Integer.toString(fields.length));
                for(int i=0;i<fields.length;i++){
                    Log.d("field",Integer.toString(i));
                    String songName=fields[i].getName().replace("_"," ");
                    SongDetails singleSong=new SongDetails(songName,artistName,R.id.songImage);
                    songsList.add(singleSong);

                }
                typeOfPlaylist="Festival";
                if(fields.length==0){
                    recyclerView.setVisibility(View.GONE);
                    noSongsFound.setVisibility(View.VISIBLE);
                }
                return songsList;

            case "Favourites":
                Toast.makeText(MainActivity.this,"This Option is currently unavailable!",Toast.LENGTH_SHORT).show();
                return songsList;

            case "Downloads":
                Toast.makeText(MainActivity.this,"Downloads",Toast.LENGTH_SHORT).show();
                mySongs = findSong(Environment.getExternalStorageDirectory());
                typeOfPlaylist="Downloads";

                for (int i = 0; i < mySongs.size(); i++) {
                    SongDetails singleSong=new SongDetails(mySongs.get(i).getName(),artistName,R.id.songImage);
                    songsList.add(singleSong);
                }

                if(mySongs.size()==0){
                    recyclerView.setVisibility(View.GONE);
                    noSongsFound.setVisibility(View.VISIBLE);
                }
                return songsList;

            case "AllSongs":
                Toast.makeText(MainActivity.this,"ALL SONGS",Toast.LENGTH_SHORT).show();
                mySongs = findSong(Environment.getExternalStorageDirectory());


                for (int i = 0; i < mySongs.size(); i++) {
                    SongDetails singleSong=new SongDetails(mySongs.get(i).getName(),artistName,R.id.songImage);
                    songsList.add(singleSong);
                }

                Field[] field=R.raw.class.getFields();
                for(int i=0;i<field.length;i++){
                    SongDetails singleSong=new SongDetails(mySongs.get(i).getName(),artistName,R.id.songImage);
                    songsList.add(singleSong);
                }

                if(mySongs.size()==0 && field.length==0){
                    recyclerView.setVisibility(View.GONE);
                    noSongsFound.setVisibility(View.VISIBLE);
                }
                return songsList;
        }


    return null;

    }

    public ArrayList<File> findSong(File file) {
        ArrayList<File> songsList=new ArrayList<>();
        File[] files=file.listFiles();


        if(files!=null){
            for(File singleFile:files)
            {
                if(singleFile.isDirectory() && !singleFile.isHidden()){
                    songsList.addAll(findSong(singleFile));
                }
                else{
                    if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav") ||  singleFile.getName().endsWith(".m4a")){
                        songsList.add(singleFile);          //Adding the mp3 and wav songs in the songsList
                    }
                }
            }
        }
        return songsList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);

        MenuItem searchItem= menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                musicLibraryAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}
