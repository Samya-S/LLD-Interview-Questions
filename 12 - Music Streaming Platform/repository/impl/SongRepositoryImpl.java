package repository.impl;

import domain.Song;
import repository.SongRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SongRepositoryImpl implements SongRepository {
    private Map<String, Song> songsById = new ConcurrentHashMap<>();
    private Map<String, String> songIdByUniqueId = new ConcurrentHashMap<>();

    @Override
    public Song save(Song song) {
        songsById.put(song.getId(), song);
        songIdByUniqueId.put(song.getSongId(), song.getId());
        return song;
    }

    @Override
    public Optional<Song> findById(String id) {
        return Optional.ofNullable(songsById.get(id));
    }

    @Override
    public Optional<Song> findBySongId(String songId) {
        String id = songIdByUniqueId.get(songId);
        if (id == null) return Optional.empty();
        return Optional.ofNullable(songsById.get(id));
    }

    @Override
    public List<Song> findByTitle(String title) {
        String lowerTitle = title.toLowerCase();
        return songsById.values().stream()
                .filter(song -> song.getTitle().toLowerCase().contains(lowerTitle))
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> findByArtistId(String artistId) {
        return songsById.values().stream()
                .filter(song -> song.getArtistId().equals(artistId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> findByAlbumId(String albumId) {
        return songsById.values().stream()
                .filter(song -> albumId != null && albumId.equals(song.getAlbumId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> findByGenre(String genre) {
        String lowerGenre = genre.toLowerCase();
        return songsById.values().stream()
                .filter(song -> song.getGenre().toLowerCase().contains(lowerGenre))
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> findAllByIds(List<String> songIds) {
        return songIds.stream()
                .map(this::findBySongId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
