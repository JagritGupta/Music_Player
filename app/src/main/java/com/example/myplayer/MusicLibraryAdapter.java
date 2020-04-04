package com.example.myplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MusicLibraryAdapter extends RecyclerView.Adapter<MusicLibraryAdapter.MusicViewHolder>  {

    Context context;
    ArrayList<SongDetails> songsList;
    ArrayList<SongDetails> searchList;
    String typeOfPlaylist="";
    private final int[] festivalSongsList = {R.raw.aa_aaye_navratre_ambe_maa, R.raw.jai_jaikaar_sukhwinder_singh, R.raw.lali_lali_laal_chunariya, R.raw.navraton_mein_ghar_mere_aayi};



    public MusicLibraryAdapter(Context context, ArrayList<SongDetails> songsList,String typeOfPlaylist) {
        this.context = context;
        this.songsList = songsList;
        this.typeOfPlaylist = typeOfPlaylist;
        this.searchList = new ArrayList<>(songsList);   //so that searchList and albumList remain different.
    }

    public MusicLibraryAdapter(Context context, String typeOfPlaylist) {
        this.context = context;
        this.typeOfPlaylist = typeOfPlaylist;
        this.searchList = new ArrayList<>(songsList);   //so that searchList and albumList remain different.
    }

    public void updateList(List<SongDetails> songsArrList) {

        songsList.clear();
        songsList.addAll(songsArrList);
        notifyDataSetChanged();
    }


    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.song_card_design, null);

        MusicViewHolder holder = new MusicViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull final MusicViewHolder holder, final int position) {

        SongDetails songDetails = songsList.get(position);
        holder.songTitle.setText(songDetails.getSongTitle());
        holder.songDesc.setText(songDetails.getSongDesc());
        Bitmap bm= BitmapFactory.decodeFile(songDetails.songAlbumArt);
        holder.songImage.setImageBitmap(bm);



        holder.songLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SongDetails songDetails =songsList.get(position);
                String songName = songsList.get(position).songTitle;
                Toast.makeText(context, songName , Toast.LENGTH_LONG).show();

                if (songDetails.position>=0) {  //Festival type
                    songDetails.setSongID(festivalSongsList[songDetails.position]);

                }
                else{
                    songDetails.path=songDetails.getPath();
                }

                Intent i = new Intent(context, PlayerActivity.class);
                i.putExtra("songObject",songDetails);
                i.putExtra("position",position);
                context.startActivity(i);
            }
        });

        holder.unFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.unFavBtn.getTag() == null) {
                    Toast.makeText(context, "Song added to Favourites", Toast.LENGTH_LONG).show();
                    holder.unFavBtn.setTag(1);
                    holder.unFavBtn.setImageResource(R.drawable.fav_filled);
                } else {
                    Toast.makeText(context, "Song removed from Favourites", Toast.LENGTH_LONG).show();
                    holder.unFavBtn.setTag(null);
                    holder.unFavBtn.setImageResource(R.drawable.fav);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return songsList.size();
    }


    class MusicViewHolder extends RecyclerView.ViewHolder {

        ImageView songImage, favBtn, unFavBtn;
        TextView songTitle, songDesc;
        RelativeLayout songLayout;

        public MusicViewHolder(@NonNull final View itemView) {

            super(itemView);
            songImage = itemView.findViewById(R.id.songImage);
            songTitle = itemView.findViewById(R.id.songTitle);
            songDesc = itemView.findViewById(R.id.songDesc);
            favBtn = itemView.findViewById(R.id.favFilled);
            unFavBtn = itemView.findViewById(R.id.favUnfilled);
            songLayout = itemView.findViewById(R.id.item_song_layout);

        }
    }
}
