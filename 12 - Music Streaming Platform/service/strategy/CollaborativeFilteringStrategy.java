package service.strategy;

import domain.ListeningHistory;
import domain.Song;
import java.util.*;

public class CollaborativeFilteringStrategy implements RecommendationStrategy {
    @Override
    public List<Song> generate(String userId, List<ListeningHistory> history) {
        // TODO: Find users with similar listening patterns
        // TODO: Recommend songs they listened to but current user hasn't
        // Complex algorithm - simplified for interview
        return Collections.emptyList();
    }
}
