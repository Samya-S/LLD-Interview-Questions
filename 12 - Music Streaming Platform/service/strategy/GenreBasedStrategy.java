package service.strategy;

import domain.ListeningHistory;
import domain.Song;
import repository.SongRepository;
import java.util.*;
import java.util.stream.Collectors;

public class GenreBasedStrategy implements RecommendationStrategy {
    private SongRepository songRepository;

    public GenreBasedStrategy(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public List<Song> generate(String userId, List<ListeningHistory> history) {
        if (history == null || history.isEmpty()) {
            return Collections.emptyList();
        }

        // Find most listened genres
        Map<String, Long> genreCount = new HashMap<>();
        for (ListeningHistory h : history) {
            // TODO: Get song genre from songId
            // For now, return empty - would need song lookup
        }

        // TODO: Return songs from top genres
        return Collections.emptyList();
    }
}
