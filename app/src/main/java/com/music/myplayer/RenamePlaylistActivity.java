package com.music.myplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.List;

public class RenamePlaylistActivity extends AppCompatDialogFragment {

    List<SongDetails> songsList;
    String oldPlaylistName,newPlaylistName;
    EditText renameBox;
    RoomService roomService;
    Application mApplication;
    MusicLibraryAdapter.IconnectPlaylist iConnectPlaylist;
    List<String> listOfPlaylists;
    public RenamePlaylistActivity() {
    }

    public RenamePlaylistActivity(List<SongDetails> songsList, String oldPlaylistName, Application application) {
        this.songsList = songsList;
        this.oldPlaylistName = oldPlaylistName;
        mApplication = application;
        roomService=new RoomService(mApplication);
    }

    public void connectingWithInteface(MusicLibraryAdapter.IconnectPlaylist iConnectPlaylist) {
        this.iConnectPlaylist = iConnectPlaylist;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_rename_playlist, null);
        renameBox = view.findViewById(R.id.rename_box);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        listOfPlaylists=Utility.getAllPlaylists(roomService);
        builder.setView(view)
                .setTitle("Rename Playlist")
                .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i=0;i<songsList.size();i++){
                            SongDetails songDetails=songsList.get(i);
                            newPlaylistName=renameBox.getText().toString();
                            //songDetails.typeOfPlaylist=songDetails.getTypeOfPlaylist().replace(oldPlaylistName,newPlaylistName);
                            roomService.setPlaylistName(newPlaylistName,songDetails.getPath());
                        }
                        int pos=listOfPlaylists.indexOf(oldPlaylistName);
                        listOfPlaylists.remove(pos);
                        listOfPlaylists.add(pos,newPlaylistName);
                        //iConnectPlaylist.renamePlaylistInAdapter(listOfPlaylists);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();

    }

}
