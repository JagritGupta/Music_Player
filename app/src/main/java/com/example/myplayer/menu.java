package com.example.myplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class menu extends AppCompatActivity {

    TextView festivalSongs;
    TextView favourites;
    TextView downloads;
    TextView allSongs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        festivalSongs=findViewById(R.id.festiveSongs);
        favourites=findViewById(R.id.favourites);
        downloads=findViewById(R.id.downloads);
        allSongs=findViewById(R.id.allSongs);



    }

    @Override
    protected void onResume() {
        super.onResume();
        runTimePermission();

    }

    public void runTimePermission(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        //albumList=displaySongsInArrayList(menuType);
                        festivalSongs.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i=new Intent(menu.this,MainActivity.class);
                                i.putExtra("type","Festival Songs");
                                startActivity(i);

                            }
                        });

                        favourites.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent i=new Intent(menu.this,MainActivity.class);
                                i.putExtra("type","Favourites");
                                startActivity(i);

                            }
                        });

                        downloads.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent i=new Intent(menu.this,MainActivity.class);
                                i.putExtra("type","Downloads");
                                startActivity(i);

                            }
                        });

                        allSongs.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i=new Intent(menu.this,MainActivity.class);
                                i.putExtra("type","AllSongs");
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

}
