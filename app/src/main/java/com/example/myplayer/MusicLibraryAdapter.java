package com.example.myplayer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MusicLibraryAdapter extends RecyclerView.Adapter<MusicLibraryAdapter.MusicViewHolder> implements Filterable {

    Context context;
    List<MusicLibrary> albumList;
    List<MusicLibrary> searchList;
    private IMusicClickListener clickListener;


    public interface IMusicClickListener {
        void onClickMusicFolder(int position);

        void onClickFavourite(int imageID);
    }

    public MusicLibraryAdapter(Context context, List<MusicLibrary> albumList, IMusicClickListener clickListener) {
        this.context = context;
        this.albumList = albumList;
        this.clickListener = clickListener;
        this.searchList = new ArrayList<>(albumList);  //so that searchList and albumList remain different.
    }

    public void updateList(List<MusicLibrary> albumList) {
        this.albumList = albumList;
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
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {

        MusicLibrary musicLibrary = albumList.get(position);
        holder.songTitle.setText(musicLibrary.getSongTitle());
        holder.songDesc.setText(musicLibrary.getSongDesc());

        //holder.songImage.setImageDrawable(context.getResources().getDrawable(musicLibrary.getSongImage()));
    }


    @Override
    public int getItemCount() {
        return albumList.size();
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
            //itemView.setOnClickListener(this);
            songImage = itemView.findViewById(R.id.songImage);
            songTitle = itemView.findViewById(R.id.songTitle);
            songDesc = itemView.findViewById(R.id.songDesc);
            favBtn = itemView.findViewById(R.id.favFilled);
            unFavBtn = itemView.findViewById(R.id.favUnfilled);
            songLayout = itemView.findViewById(R.id.item_song_layout);

            songLayout.setOnClickListener(new View.OnClickListener() {
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
            });
        }




       /*@Override
        public void onClick(View v) {
            if(v.getId()==R.id.favUnfilled){
                unFavBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onClickFavourite();

                    }
                });
            }

            else {
                songLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onClickMusicFolder(albumList.get(getAdapterPosition()),getAdapterPosition());
                    }
                });
            }
       }*/
    }
}
