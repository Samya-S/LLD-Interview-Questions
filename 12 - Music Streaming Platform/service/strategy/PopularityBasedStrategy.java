package service.strategy;

import domain.ListeningHistory;
import domain.Song;
import repository.SongRepository;
import java.util.*;

public class PopularityBasedStrategy implements RecommendationStrategy {
    private SongRepository songRepository;

    public PopularityBasedStrategy(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public List<Song> generate(String userId, List<ListeningHistory> history) {
        // TODO: Return most popular songs (would need popularity score in Song entity)
        // For interview, return random songs
        return Collections.emptyList();
    }
}
