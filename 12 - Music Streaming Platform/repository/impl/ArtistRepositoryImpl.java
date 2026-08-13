package repository.impl;

import domain.Artist;
import repository.ArtistRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ArtistRepositoryImpl implements ArtistRepository {
    private Map<String, Artist> artistsById = new ConcurrentHashMap<>();
    private Map<String, String> artistIdByUniqueId = new ConcurrentHashMap<>();

    @Override
    public Artist save(Artist artist) {
        artistsById.put(artist.getId(), artist);
        artistIdByUniqueId.put(artist.getArtistId(), artist.getId());
        return artist;
    }

    @Override
    public Optional<Artist> findById(String id) {
        return Optional.ofNullable(artistsById.get(id));
    }

    @Override
    public Optional<Artist> findByArtistId(String artistId) {
        String id = artistIdByUniqueId.get(artistId);
        if (id == null) return Optional.empty();
        return Optional.ofNullable(artistsById.get(id));
    }

    @Override
    public List<Artist> findByName(String name) {
        String lowerName = name.toLowerCase();
        return artistsById.values().stream()
                .filter(artist -> artist.getName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }
}
