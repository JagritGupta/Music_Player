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

public class MusicLibraryAdapter extends RecyclerView.Adapter<MusicLibraryAdapter.MusicViewHolder> {

    Context context;
    ArrayList<SongDetails> songsList = Utility.songsList;
    ArrayList<SongDetails> searchList;
    String typeOfPlaylist = "";


    public MusicLibraryAdapter(Context context, ArrayList<SongDetails> songsList, String typeOfPlaylist) {
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

        final SongDetails songDetails = songsList.get(position);

        if (songDetails.playlistType.equalsIgnoreCase("Festival")) {
            holder.songTitle.setText(songDetails.getSongTitle());
            holder.songDesc.setText(songDetails.getSongDesc());
            holder.songImage.setImageResource(R.drawable.music_player);
        }
        else if(songDetails.playlistType.equalsIgnoreCase("Downloads")) {
            holder.songTitle.setText(songDetails.getSongTitle());
            holder.songDesc.setText(songDetails.getSongDesc());
            Bitmap bm = BitmapFactory.decodeByteArray(songDetails.songAlbumArt, 0, songDetails.songAlbumArt.length);
            holder.songImage.setImageBitmap(bm);
        }

        if (songDetails.isFavourite()) {
            holder.unFavBtn.setTag(1);
            holder.unFavBtn.setImageResource(R.drawable.fav_filled);
        } else {
            holder.unFavBtn.setTag(null);
            holder.unFavBtn.setImageResource(R.drawable.fav);
        }

        holder.songLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String songName = songsList.get(position).songTitle;
                Toast.makeText(context, songName, Toast.LENGTH_LONG).show();
                Intent i = new Intent(context, PlayerActivity.class);
                i.putExtra("position", position);
                if (typeOfPlaylist == "Favourites") {
                    i.putExtra("isFavouriteMenu", true);
                } else {
                    i.putExtra("isFavouriteMenu", false);
                }
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
                    songDetails.setIsFavourite(true);
                } else {
                    Toast.makeText(context, "Song removed from Favourites", Toast.LENGTH_LONG).show();
                    holder.unFavBtn.setTag(null);
                    holder.unFavBtn.setImageResource(R.drawable.fav);
                    songDetails.setIsFavourite(false);
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
