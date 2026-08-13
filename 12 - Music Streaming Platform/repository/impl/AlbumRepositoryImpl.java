package repository.impl;

import domain.Album;
import repository.AlbumRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AlbumRepositoryImpl implements AlbumRepository {
    private Map<String, Album> albumsById = new ConcurrentHashMap<>();
    private Map<String, String> albumIdByUniqueId = new ConcurrentHashMap<>();

    @Override
    public Album save(Album album) {
        albumsById.put(album.getId(), album);
        albumIdByUniqueId.put(album.getAlbumId(), album.getId());
        return album;
    }

    @Override
    public Optional<Album> findById(String id) {
        return Optional.ofNullable(albumsById.get(id));
    }

    @Override
    public Optional<Album> findByAlbumId(String albumId) {
        String id = albumIdByUniqueId.get(albumId);
        if (id == null) return Optional.empty();
        return Optional.ofNullable(albumsById.get(id));
    }

    @Override
    public List<Album> findByArtistId(String artistId) {
        return albumsById.values().stream()
                .filter(album -> album.getArtistId().equals(artistId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Album> findByTitle(String title) {
        String lowerTitle = title.toLowerCase();
        return albumsById.values().stream()
                .filter(album -> album.getTitle().toLowerCase().contains(lowerTitle))
                .collect(Collectors.toList());
    }
}
