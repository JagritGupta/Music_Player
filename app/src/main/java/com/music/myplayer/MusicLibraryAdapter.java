package com.music.myplayer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MusicLibraryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    FragmentManager fragManager;
    private IconnectPlaylist iConnectPlaylist;
    String currentPlaylistName = " ";
    Application application;
    Boolean isDeleteMenuOptionAvailable;
    Context context;
    ArrayList<SongDetails> songsList = Utility.songsList;
    ArrayList<SongDetails> searchList;
    List<String> listOfPlaylists;

    int[] colors = {
            R.color.darkBlue,
            R.color.yellow,
            R.color.pink,
            R.color.darkGreen,
            R.color.violet,
    };
    int colorCounter=0;

    private RoomService roomService;
    Activity mActivity;
    String getViewType;
    private final int TYPE_PLAYLIST_OPTION = 1;
    private final int TYPE_SONGS_LIST = 2;

    //Main Activity normal songs
    public MusicLibraryAdapter(Context context, ArrayList<SongDetails> songsList, FragmentManager fragmentManager, Application application, Activity activity, String getViewType, Boolean isDeleteMenuOptionAvailable) {
        this.context = context;
        this.songsList = songsList;
        this.searchList = new ArrayList<>(songsList);   //so that searchList and albumList remain different.
        this.application = application;
        fragManager = fragmentManager;
        mActivity = activity;
        this.getViewType = getViewType;
        this.isDeleteMenuOptionAvailable = isDeleteMenuOptionAvailable;
    }


    public MusicLibraryAdapter(Context context, List<String> namesOfPlaylists, Application application, Activity activity, String getViewType,FragmentManager fragManager) {
        this.context = context;
        this.listOfPlaylists = namesOfPlaylists;
        mActivity = activity;
        this.getViewType = getViewType;
        this.fragManager=fragManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        roomService = new RoomService(application);

        switch (viewType) {
            case TYPE_SONGS_LIST:
                view = inflater.inflate(R.layout.song_card_design, null);
                MusicViewHolder holder = new MusicViewHolder(view);
                return holder;

            case TYPE_PLAYLIST_OPTION:
                view = inflater.inflate(R.layout.playlist_card_design, null);
                PlaylistViewHolder playlistViewHolder = new PlaylistViewHolder(view);
                return playlistViewHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof MusicViewHolder) {

            final SongDetails songDetails = songsList.get(position);

            if (songDetails.playerType.equalsIgnoreCase("Festival")) {
                ((MusicViewHolder) holder).songTitle.setText(songDetails.getSongTitle());
                ((MusicViewHolder) holder).songDesc.setText(songDetails.getSongDesc());
                ((MusicViewHolder) holder).songImage.setImageResource(songDetails.getFestiwalDrawable());
            } else if (songDetails.playerType.equalsIgnoreCase("Downloads")) {
                ((MusicViewHolder) holder).songTitle.setText(songDetails.getSongTitle());
                ((MusicViewHolder) holder).songDesc.setText(songDetails.getSongDesc());
                if (songDetails.songAlbumArt != null && songDetails.songAlbumArt.length > 0) {
                    Bitmap bm = BitmapFactory.decodeByteArray(songDetails.songAlbumArt, 0, songDetails.songAlbumArt.length);
                    ((MusicViewHolder) holder).songImage.setImageBitmap(bm);
                }
            }


            ((MusicViewHolder) holder).songLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String songName = songsList.get(position).songTitle;
                    Intent i = new Intent(context, PlayerActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("position", position);
                    if (songDetails.typeOfPlaylist == "Favourites") {
                        i.putExtra("isFavouriteMenu", true);
                    } else {
                        i.putExtra("isFavouriteMenu", false);
                    }
                    MainActivity.isMainActivityVisible = false;

                    context.startActivity(i);
                }
            });


            ((MusicViewHolder) holder).menuOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, ((MusicViewHolder) holder).menuOption);
                    popupMenu.inflate(R.menu.menu_option);
                    if (isDeleteMenuOptionAvailable)
                        popupMenu.getMenu().findItem(R.id.delete_song_from_playlist).setVisible(true);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.add_to_playlist:
                                    CreateNewPlaylistDialog createNewPlaylistDialog = new CreateNewPlaylistDialog(application, songDetails);
                                    createNewPlaylistDialog.show(fragManager, "");
                                    return true;

                                case R.id.delete_song_from_playlist:
                                    String playlist = songDetails.getTypeOfPlaylist();
                                    playlist = playlist.replace(currentPlaylistName, "");
                                    roomService.setPlaylistName(playlist, songDetails.getPath());
                                    songsList.remove(songDetails);
                                    songDetails.setTypeOfPlaylist(playlist);
                                    notifyDataSetChanged();
                                    return true;

                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.show();
                }
            });

        } else if (holder instanceof PlaylistViewHolder) {
            ((PlaylistActivity) context).getSupportActionBar().setTitle("Playlists");

            listOfPlaylists=Utility.getAllPlaylists(roomService);
            currentPlaylistName = listOfPlaylists.get(position);
            ((PlaylistViewHolder) holder).currentPlaylist.setText(currentPlaylistName);
            if(colorCounter>=5)
                colorCounter=0;
            int color = colors[colorCounter];
            colorCounter+=1;
            ((PlaylistViewHolder) holder).currentPlaylist.setBackgroundResource(color);

            if(listOfPlaylists.size()==0){

            }
            ((PlaylistViewHolder) holder).currentPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPlaylistName = listOfPlaylists.get(position);
                    songsList=(ArrayList<SongDetails>) roomService.getSongsFromPlaylist(currentPlaylistName);
                    iConnectPlaylist.sendSongsPlaylist(songsList);
                    Utility.setSongsList(songsList);
                    colorCounter=0;
                    notifyDataSetChanged();
                }
            });

            ((PlaylistViewHolder) holder).currentPlaylist.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    currentPlaylistName = listOfPlaylists.get(position);
                    PopupMenu popupMenu = new PopupMenu(context, ((PlaylistViewHolder) holder).currentPlaylist);
                    popupMenu.inflate(R.menu.menu_option);
                    popupMenu.getMenu().findItem(R.id.delete_song_from_playlist).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.add_to_playlist).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete_playlist).setVisible(true);
                    //popupMenu.getMenu().findItem(R.id.rename_playlist).setVisible(true);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete_playlist:
                                    Toast.makeText(context,"Playlist Deleted",Toast.LENGTH_SHORT).show();
                                    List<SongDetails> songsFromPlaylist = roomService.getSongsFromPlaylist(currentPlaylistName);
                                    deletingPlaylist(songsFromPlaylist, currentPlaylistName);
                                    listOfPlaylists.remove(currentPlaylistName);
                                    notifyDataSetChanged();
                                    return true;

                                /*case R.id.rename_playlist:
                                    RenamePlaylistActivity renamePlaylistDialog = new RenamePlaylistActivity(roomService.getSongsFromPlaylist(currentPlaylistName), currentPlaylistName, application);
                                    renamePlaylistDialog.show(fragManager, "");
                                    renamePlaylistDialog.connectingWithInteface(new IconnectPlaylist() {
                                        @Override
                                        public void sendSongsPlaylist(ArrayList<SongDetails> songsList) {

                                        }

                                        @Override
                                        public void renamePlaylistInAdapter(List<String> listOfPlaylist) {

                                            listOfPlaylists=Utility.getAllPlaylists(roomService);
                                            notifyDataSetChanged();

                                        }
                                    });
                                    notifyDatasetChanged
                                    return true;*/
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                    return true;
                }
            });

        }

    }

    private void deletingPlaylist(List<SongDetails> songsList, String playlistName) {
        for (int i = 0; i < songsList.size(); i++) {
            SongDetails songDetails = songsList.get(i);
            String newPlaylist = songDetails.getTypeOfPlaylist().replace("+" + playlistName + "+", "");
            roomService.setPlaylistName(newPlaylist, songDetails.getPath());
        }
    }



    @Override
    public int getItemViewType(int position) {
        switch (getViewType) {
            case "SongPlayer":
                return TYPE_SONGS_LIST;
            case "Playlist":
                return TYPE_PLAYLIST_OPTION;
            default:
                return position;
        }
    }

    @Override
    public int getItemCount() {
        switch (getViewType) {
            case "SongPlayer":
                return songsList.size();
            case "Playlist":
                return listOfPlaylists.size();
            default:
                return 0;
        }
    }

    class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        private ImageView songImage;
        private TextView menuOption;
        private TextView songTitle, songDesc;
        private RelativeLayout songLayout;

        public MusicViewHolder(@NonNull final View itemView) {

            super(itemView);
            songImage = itemView.findViewById(R.id.songImage);
            songTitle = itemView.findViewById(R.id.songTitle);
            songDesc = itemView.findViewById(R.id.songDesc);
            menuOption = itemView.findViewById(R.id.menuOptions);
            songLayout = itemView.findViewById(R.id.item_song_layout);

        }


        @Override
        public void onClick(View view) {

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {

                case R.id.add_to_playlist:
                    return true;
                case R.id.delete_song_from_playlist:
                    return true;

                default:
                    return false;
            }
        }


    }

    class PlaylistViewHolder extends RecyclerView.ViewHolder {

        private TextView currentPlaylist;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            currentPlaylist = itemView.findViewById(R.id.playlistName);

        }
    }

    public void connectingWithInterface(IconnectPlaylist iConnectPlaylist) {
        this.iConnectPlaylist = iConnectPlaylist;
    }

    public interface IconnectPlaylist {
        void sendSongsPlaylist(ArrayList<SongDetails> songsList);
        //void renamePlaylistInAdapter(List<String> listOfPlaylists);
    }

    public void setData(Context context, ArrayList<SongDetails> songsList, Application application, Activity activity, String getViewType) {
        this.context = context;
        this.songsList = songsList;
        this.searchList = new ArrayList<>(songsList);   //so that searchList and albumList remain different.
        this.application = application;
        mActivity = activity;
        this.getViewType = getViewType;
    }

    public void updateList(List<SongDetails> songsArrList) {
        songsList.clear();
        songsList.addAll(songsArrList);
        notifyDataSetChanged();
    }

}
