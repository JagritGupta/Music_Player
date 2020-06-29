package com.example.myplayer;

public interface IdialogListener {
    public void createNewPlaylist(String newPlaylistName,SongDetails songDetails);

    public void addSongToPlaylist(String playlistName);

}
