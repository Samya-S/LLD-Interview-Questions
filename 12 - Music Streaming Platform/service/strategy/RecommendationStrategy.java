package service.strategy;

import domain.ListeningHistory;
import domain.Song;
import java.util.List;

public interface RecommendationStrategy {
    List<Song> generate(String userId, List<ListeningHistory> history);
}
