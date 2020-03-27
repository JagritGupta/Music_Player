package com.example.myplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView noSongsFound;
    MusicLibraryAdapter musicLibraryAdapter;
    List<MusicLibrary> albumList=null;
    String artistName="Artist Name";
    String[] items;
    ArrayList<File> mySongs;
    SongDetails songDetails;
    String menuType,typeOfPlaylist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        albumList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        noSongsFound=findViewById(R.id.noDataFound);
        songDetails=SongDetails.getInstance();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent i=getIntent();
        menuType=i.getStringExtra("type");
        albumList=displaySongsInArrayList(menuType);



        musicLibraryAdapter = new MusicLibraryAdapter(this, albumList,typeOfPlaylist);
            /*public void onClickMusicFolder(int position) {
                String songName=albumList.get(position).songTitle;
                Toast.makeText(MainActivity.this,albumList.get(position).songTitle,Toast.LENGTH_LONG).show();
                Intent i=new Intent(getApplicationContext(),PlayerActivity.class);
                songDetails.songTitle=songName;
                songDetails.position=position;
                songDetails.songDesc=artistName;
                songDetails.imageId=R.id.songImage;
                i.putExtra("songsArrayList",mySongs);
                i.putExtra("songsDetailsObject",songDetails);
                i.putExtra("typeOfPlaylist",typeOfPlaylist);
                startActivity(i);
            }

            public void onClickFavourite(int imageID) {
                Toast.makeText(MainActivity.this,"On ClickFavourite",Toast.LENGTH_LONG).show();
                int id=R.id.favUnfilled;
                if(imageID==id){
                    Toast.makeText(MainActivity.this,"Song added to favourite",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(MainActivity.this,"Song removed",Toast.LENGTH_LONG).show();

                }
            }
        });*/

        /*musicLibraryAdapter.setClickListener(new MusicLibraryAdapter.IMusicClickListener() {
            @Override
            public void onClickMusicFolder(MusicLibrary album, int position) {
                String songName=albumList.get(position).songTitle;
                Toast.makeText(MainActivity.this,album.songTitle,Toast.LENGTH_LONG).show();
                Intent i=new Intent(getApplicationContext(),PlayerActivity.class);
                songDetails.songTitle=songName;
                songDetails.position=position;
                songDetails.songDesc=artistName;
                songDetails.imageId=R.id.songImage;
                i.putExtra("songsArrayList",mySongs);
                startActivity(i);
            }

            @Override
            public void onClickFavourite() {

            }
        });*/



        recyclerView.setAdapter(musicLibraryAdapter);




    }



    public List<MusicLibrary> displaySongsInArrayList(String menuType) {
        switch (menuType){
            case "Festival Songs":
                Toast.makeText(MainActivity.this,"FESTIVAL SONGS",Toast.LENGTH_SHORT).show();
                Field[] fields=R.raw.class.getFields();
                Log.d("field",Integer.toString(fields.length));
                for(int i=0;i<fields.length;i++){
                    Log.d("field",Integer.toString(i));
                    String songName=fields[i].getName().replace("_"," ");
                    MusicLibrary singleSong=new MusicLibrary(songName,artistName,R.id.songImage);
                    albumList.add(singleSong);

                }
                typeOfPlaylist="Festival";
                if(fields.length==0){
                    recyclerView.setVisibility(View.GONE);
                    noSongsFound.setVisibility(View.VISIBLE);
                }
                return albumList;

            case "Favourites":
                Toast.makeText(MainActivity.this,"This Option is currently unavailable!",Toast.LENGTH_SHORT).show();
                return albumList;
            case "Downloads":
                Toast.makeText(MainActivity.this,"Downloads",Toast.LENGTH_SHORT).show();
                mySongs = findSong(Environment.getExternalStorageDirectory());
                typeOfPlaylist="Downloads";
                items=new String[mySongs.size()];

                for (int i = 0; i < mySongs.size(); i++) {
                   MusicLibrary singleSong=new MusicLibrary(mySongs.get(i).getName(),artistName,R.id.songImage);
                    albumList.add(singleSong);
                }

                if(mySongs.size()==0){
                    recyclerView.setVisibility(View.GONE);
                    noSongsFound.setVisibility(View.VISIBLE);
                }
                return albumList;

            case "AllSongs":
                Toast.makeText(MainActivity.this,"ALL SONGS",Toast.LENGTH_SHORT).show();
                mySongs = findSong(Environment.getExternalStorageDirectory());

                items=new String[mySongs.size()];

                for (int i = 0; i < mySongs.size(); i++) {
                    MusicLibrary singleSong=new MusicLibrary(mySongs.get(i).getName(),artistName,R.id.songImage);
                    albumList.add(singleSong);
                }


                Field[] field=R.raw.class.getFields();
                for(int i=0;i<field.length;i++){
                    MusicLibrary singleSong=new MusicLibrary(field[i].getName(),artistName,R.id.songImage);
                    albumList.add(singleSong);
                }

                if(mySongs.size()==0 && field.length==0){
                    recyclerView.setVisibility(View.GONE);
                    noSongsFound.setVisibility(View.VISIBLE);
                }
                return albumList;
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
