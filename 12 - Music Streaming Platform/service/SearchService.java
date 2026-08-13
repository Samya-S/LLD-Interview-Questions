package service;

import domain.Album;
import domain.Artist;
import domain.Song;
import repository.AlbumRepository;
import repository.ArtistRepository;
import repository.SongRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchService {
    private SongRepository songRepository;
    private ArtistRepository artistRepository;
    private AlbumRepository albumRepository;

    public SearchService(SongRepository songRepository, ArtistRepository artistRepository, 
                         AlbumRepository albumRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
    }

    public SearchResponse search(String query, String type) {
        SearchResponse response = new SearchResponse();
        response.setSongs(songRepository.findByTitle(query));
        response.setArtists(artistRepository.findByName(query));
        response.setAlbums(albumRepository.findByTitle(query));
        return response;
    }

    public static class SearchResponse {
        private List<Song> songs = new ArrayList<>();
        private List<Artist> artists = new ArrayList<>();
        private List<Album> albums = new ArrayList<>();

        public List<Song> getSongs() {
            return songs;
        }

        public void setSongs(List<Song> songs) {
            this.songs = songs;
        }

        public List<Artist> getArtists() {
            return artists;
        }

        public void setArtists(List<Artist> artists) {
            this.artists = artists;
        }

        public List<Album> getAlbums() {
            return albums;
        }

        public void setAlbums(List<Album> albums) {
            this.albums = albums;
        }
    }
}
