package repository;

import domain.ListeningHistory;
import java.util.List;

public interface ListeningHistoryRepository {
    void save(ListeningHistory history);
    List<ListeningHistory> findByUserId(String userId);
    List<ListeningHistory> findByUserIdOrderByPlayedAtDesc(String userId, int limit);
    // TODO: Find existing history for today to update instead of creating new
    ListeningHistory findByUserIdAndSongIdAndDate(String userId, String songId, long date);
}
