package repository;

import domain.Album;
import java.util.List;
import java.util.Optional;

public interface AlbumRepository {
    Album save(Album album);
    Optional<Album> findById(String id);
    Optional<Album> findByAlbumId(String albumId);
    List<Album> findByArtistId(String artistId);
    List<Album> findByTitle(String title);
}
