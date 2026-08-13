package service;

import domain.Playlist;
import domain.Song;
import repository.PlaylistRepository;
import repository.SongRepository;
import java.util.*;

public class PlaylistService {
    private PlaylistRepository playlistRepository;
    private SongRepository songRepository;
    private LockService lockService;

    public PlaylistService(PlaylistRepository playlistRepository, SongRepository songRepository, 
                          LockService lockService) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
        this.lockService = lockService;
    }

    public Playlist createPlaylist(String userId, String name, List<String> songIds) {
        // log in and userId Checks
        // Validate songs exist
        if (songIds != null && !songIds.isEmpty()) {
            List<Song> songs = songRepository.findAllByIds(songIds);
            if (songs.size() != songIds.size()) {
                throw new IllegalArgumentException("Some songs not found");
            }
        }

        String id = UUID.randomUUID().toString();
        String playlistId = "PL_" + id.substring(0, 8);
        long now = System.currentTimeMillis();
        
        Playlist playlist = new Playlist(id, playlistId, name, userId, false, 
                                        songIds != null ? songIds : new ArrayList<>(), now, now);
        return playlistRepository.save(playlist);
    }

    public Playlist updatePlaylist(String playlistId, String userId, String name, List<String> songIds) {
        String lockKey = "playlist_lock_" + playlistId;

        // Try this for some time else return by telling the client 
        // Someone else is updating it 
        if (!lockService.acquire(lockKey, 500)) {
            throw new IllegalStateException("Playlist is being updated by another request");
        }

        try {
            Optional<Playlist> playlistOpt = playlistRepository.findByPlaylistId(playlistId);
            if (playlistOpt.isEmpty()) {
                throw new IllegalArgumentException("Playlist not found: " + playlistId);
            }

            Playlist playlist = playlistOpt.get();
            if (!playlist.getUserId().equals(userId)) {
                throw new IllegalArgumentException("User does not own this playlist");
            }

            // Validate songs exist
            if (songIds != null && !songIds.isEmpty()) {
                List<Song> songs = songRepository.findAllByIds(songIds);
                if (songs.size() != songIds.size()) {
                    throw new IllegalArgumentException("Some songs not found");
                }
            }

            playlist.setName(name != null ? name : playlist.getName());
            if (songIds != null) {
                playlist.setSongIds(songIds);
            }
            playlist.setUpdatedAt(System.currentTimeMillis());
            
            return playlistRepository.save(playlist);
        } finally {
            lockService.release(lockKey);
        }
    }

    public void deletePlaylist(String playlistId, String userId) {
        Optional<Playlist> playlistOpt = playlistRepository.findByPlaylistId(playlistId);
        if (playlistOpt.isEmpty()) {
            throw new IllegalArgumentException("Playlist not found: " + playlistId);
        }

        Playlist playlist = playlistOpt.get();
        if (!playlist.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User does not own this playlist");
        }

        playlistRepository.delete(playlistId);
    }

    public Playlist addSongs(String playlistId, String userId, List<String> songIds) {
        // Validate songs exist
        List<Song> songs = songRepository.findAllByIds(songIds);
        if (songs.size() != songIds.size()) {
            throw new IllegalArgumentException("Some songs not found");
        }

        String lockKey = "playlist_lock_" + playlistId;
        if (!lockService.acquire(lockKey, 500)) {
            throw new IllegalStateException("Playlist is being updated by another request");
        }

        try {
            Optional<Playlist> playlistOpt = playlistRepository.findByPlaylistId(playlistId);
            if (playlistOpt.isEmpty()) {
                throw new IllegalArgumentException("Playlist not found: " + playlistId);
            }

            Playlist playlist = playlistOpt.get();
            if (!playlist.getUserId().equals(userId)) {
                throw new IllegalArgumentException("User does not own this playlist");
            }

            List<String> currentSongs = new ArrayList<>(playlist.getSongIds());
            for (String songId : songIds) {
                if (!currentSongs.contains(songId)) {
                    currentSongs.add(songId);
                }
            }
            playlist.setSongIds(currentSongs);
            playlist.setUpdatedAt(System.currentTimeMillis());
            
            return playlistRepository.save(playlist);
        } finally {
            lockService.release(lockKey);
        }
    }

    public Playlist removeSongs(String playlistId, String userId, List<String> songIds) {
        String lockKey = "playlist_lock_" + playlistId;
        if (!lockService.acquire(lockKey, 500)) {
            throw new IllegalStateException("Playlist is being updated by another request");
        }

        try {
            Optional<Playlist> playlistOpt = playlistRepository.findByPlaylistId(playlistId);
            if (playlistOpt.isEmpty()) {
                throw new IllegalArgumentException("Playlist not found: " + playlistId);
            }

            Playlist playlist = playlistOpt.get();
            if (!playlist.getUserId().equals(userId)) {
                throw new IllegalArgumentException("User does not own this playlist");
            }

            List<String> currentSongs = new ArrayList<>(playlist.getSongIds());
            currentSongs.removeAll(songIds);
            playlist.setSongIds(currentSongs);
            playlist.setUpdatedAt(System.currentTimeMillis());
            
            return playlistRepository.save(playlist);
        } finally {
            lockService.release(lockKey);
        }
    }
}
