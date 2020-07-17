package com.music.myplayer;

public interface IdialogListener {
    public void createNewPlaylist(String newPlaylistName,SongDetails songDetails);

    public void addSongToPlaylist(String playlistName);

}
