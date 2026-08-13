package controller;

import domain.Playlist;
import service.PlaylistService;
import java.util.List;

public class PlaylistController {
    private PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    public Playlist createPlaylist(String userId, String name, List<String> songIds) {
        return playlistService.createPlaylist(userId, name, songIds);
    }

    public Playlist updatePlaylist(String playlistId, String userId, String name, List<String> songIds) {
        return playlistService.updatePlaylist(playlistId, userId, name, songIds);
    }

    public void deletePlaylist(String playlistId, String userId) {
        playlistService.deletePlaylist(playlistId, userId);
    }

    public Playlist addSongs(String playlistId, String userId, List<String> songIds) {
        return playlistService.addSongs(playlistId, userId, songIds);
    }

    public Playlist removeSongs(String playlistId, String userId, List<String> songIds) {
        return playlistService.removeSongs(playlistId, userId, songIds);
    }
}
