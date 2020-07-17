package com.music.myplayer;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.ArrayList;
import java.util.List;


public class CreateNewPlaylistDialog extends AppCompatDialogFragment {
    private SongDetails songDetails;
    EditText editBox;
    String newPlaylistName;
    LinearLayout linearLayout;
    static IdialogListener iDialogListener;
    ArrayList<String> selectedCheckBoxes;
    TableRow tableRow;
    RoomService roomService;
    Application mApplication;
    private List<String> listOfPlaylists;

    public CreateNewPlaylistDialog(Application application, SongDetails songDetails) {
        mApplication = application;
        this.songDetails = songDetails;
    }

    public static void getCallback(IdialogListener iDialogListeners) {
        iDialogListener = iDialogListeners;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.create_playlist_dialog, null);
        roomService = new RoomService(mApplication);
        tableRow = view.findViewById(R.id.table_row);
        linearLayout = view.findViewById(R.id.linear_create);
        selectedCheckBoxes = new ArrayList<>();
        editBox = view.findViewById(R.id.new_playlist_box);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        listOfPlaylists = Utility.getAllPlaylists(roomService);
        for (int i = 0; i < listOfPlaylists.size(); i++) {
            tableRow = new TableRow(getContext());

            tableRow.setId(i);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            final CheckBox checkBox = new CheckBox(getContext());
            checkBox.setId(i);
            final String currentPlaylist = listOfPlaylists.get(i);
            checkBox.setText(currentPlaylist);
            tableRow.addView(checkBox);
            linearLayout.addView(tableRow);


            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked && !selectedCheckBoxes.contains(currentPlaylist)) {
                        selectedCheckBoxes.add(currentPlaylist);
                        Log.d("checking", String.valueOf(currentPlaylist));
                    } else if (!isChecked && selectedCheckBoxes.contains(currentPlaylist)) {
                        selectedCheckBoxes.remove(currentPlaylist);
                        Log.d("checking", String.valueOf(currentPlaylist));
                    }
                }
            });

        }

        builder.setView(view)
                .setTitle("New Playlist")

                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        newPlaylistName = editBox.getText().toString();
                        if (!newPlaylistName.isEmpty()) {
                            if(!listOfPlaylists.contains(newPlaylistName)){
                                Toast.makeText(getContext(), "Song Added To Playlist", Toast.LENGTH_SHORT).show();
                                iDialogListener.createNewPlaylist(newPlaylistName,songDetails);
                            }
                            else{
                                Toast.makeText(getContext(), "ALREADY EXIST", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (selectedCheckBoxes.size() > 0) {
                            Toast.makeText(getContext(), "Song Added To Playlist", Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < selectedCheckBoxes.size(); i++) {
                                String currentName="+"+selectedCheckBoxes.get(i)+"+";
                                if(!songDetails.getTypeOfPlaylist().contains(currentName)){
                                    String playlistName = songDetails.getTypeOfPlaylist() + currentName;
                                    songDetails.setTypeOfPlaylist(playlistName);
                                    roomService.setPlaylistName(playlistName, songDetails.getSongPath());
                                }
                            }
                        }
                        if (selectedCheckBoxes.size() <= 0 && newPlaylistName.isEmpty()) {
                            Toast.makeText(getContext(), "Please select or enter a1 Playlist Name", Toast.LENGTH_SHORT).show();
                        }
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
