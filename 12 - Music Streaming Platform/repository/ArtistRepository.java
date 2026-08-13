package repository;

import domain.Artist;
import java.util.List;
import java.util.Optional;

public interface ArtistRepository {
    Artist save(Artist artist);
    Optional<Artist> findById(String id);
    Optional<Artist> findByArtistId(String artistId);
    List<Artist> findByName(String name);
}
