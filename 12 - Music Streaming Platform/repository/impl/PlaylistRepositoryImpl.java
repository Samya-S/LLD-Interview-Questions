package repository.impl;

import domain.Playlist;
import repository.PlaylistRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PlaylistRepositoryImpl implements PlaylistRepository {
    private Map<String, Playlist> playlistsById = new ConcurrentHashMap<>();
    private Map<String, String> playlistIdByUniqueId = new ConcurrentHashMap<>();

    @Override
    public Playlist save(Playlist playlist) {
        playlistsById.put(playlist.getId(), playlist);
        playlistIdByUniqueId.put(playlist.getPlaylistId(), playlist.getId());
        return playlist;
    }

    @Override
    public Optional<Playlist> findById(String id) {
        return Optional.ofNullable(playlistsById.get(id));
    }

    @Override
    public Optional<Playlist> findByPlaylistId(String playlistId) {
        String id = playlistIdByUniqueId.get(playlistId);
        if (id == null) return Optional.empty();
        return Optional.ofNullable(playlistsById.get(id));
    }

    @Override
    public List<Playlist> findByUserId(String userId) {
        return playlistsById.values().stream()
                .filter(playlist -> playlist.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String playlistId) {
        String id = playlistIdByUniqueId.remove(playlistId);
        if (id != null) {
            playlistsById.remove(id);
        }
    }
}
