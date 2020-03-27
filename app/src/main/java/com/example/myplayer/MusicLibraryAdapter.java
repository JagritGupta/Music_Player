package com.example.myplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicLibraryAdapter extends RecyclerView.Adapter<MusicLibraryAdapter.MusicViewHolder> implements Filterable {

    Context context;
    List<MusicLibrary> songsList;
    List<MusicLibrary> searchList;
    String typeOfPlaylist;
    private IMusicClickListener clickListener;


    public interface IMusicClickListener {
        void onClickMusicFolder(int position);

        void onClickFavourite(int imageID);
    }

    public MusicLibraryAdapter(Context context, List<MusicLibrary> songsList,String typeOfPlaylist) {
        this.context = context;
        this.songsList = songsList;
        this.clickListener = clickListener;
        this.typeOfPlaylist=typeOfPlaylist;
        this.searchList = new ArrayList<>(songsList);   //so that searchList and albumList remain different.
    }

    public void updateList(List<MusicLibrary> albumList) {
        this.songsList = albumList;
    }

    public void setClickListener(IMusicClickListener clickListener, List<MusicLibrary> albumList) {
        this.clickListener = clickListener;
        updateList(albumList);
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

        MusicLibrary musicLibrary = songsList.get(position);
        holder.songTitle.setText(musicLibrary.getSongTitle());
        holder.songDesc.setText(musicLibrary.getSongDesc());

        holder.songLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songName= songsList.get(position).songTitle;
                Toast.makeText(context, songsList.get(position).songTitle,Toast.LENGTH_LONG).show();
                Intent i=new Intent(context,PlayerActivity.class);

                SongDetails songDetails=SongDetails.getInstance();
                songDetails.songTitle=songName;
                songDetails.position=position;
                songDetails.songDesc="Artist name";
                songDetails.imageId=R.id.songImage;
                songDetails.songID= 0;

                i.putExtra("songsDetailsObject",songDetails);
                i.putExtra("typeOfPlaylist",typeOfPlaylist);
                context.startActivity(i);
            }
        });

        holder.unFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.unFavBtn.getTag()==null){
                    Toast.makeText(context,"Song added to Favourites",Toast.LENGTH_LONG).show();
                    holder.unFavBtn.setTag(1);
                    holder.unFavBtn.setImageResource(R.drawable.fav_filled);
                }else{
                    Toast.makeText(context,"Song removed from Favourites",Toast.LENGTH_LONG).show();
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

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private Filter mFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MusicLibrary> filteredList=new ArrayList<>();

            if(constraint==null || constraint.length()==0){
                filteredList.addAll(searchList);
            }
            else{
                String filterpattern=constraint.toString().toLowerCase().trim();
                for(MusicLibrary item:searchList){
                    if(item.getSongTitle().toLowerCase().contains(filterpattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results=new FilterResults();
            results.values=filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //To publish result over ui
            searchList.clear();
            searchList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

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

            /*songLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClickMusicFolder(getAdapterPosition());
                }
            });

            unFavBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int imageID = unFavBtn.getId();
                    clickListener.onClickFavourite(imageID);

                }
            });*/
        }
    }
}
