package repository;

import domain.Playlist;
import java.util.List;
import java.util.Optional;

public interface PlaylistRepository {
    Playlist save(Playlist playlist);
    Optional<Playlist> findById(String id);
    Optional<Playlist> findByPlaylistId(String playlistId);
    List<Playlist> findByUserId(String userId);
    void delete(String playlistId);
}
